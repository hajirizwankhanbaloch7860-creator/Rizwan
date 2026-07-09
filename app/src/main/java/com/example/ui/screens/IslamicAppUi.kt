package com.example.ui.screens

import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.*
import com.example.data.model.*
import com.example.ui.*
import com.example.utils.IslamicDateTimeUtils
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.QuranScheherazadeFontFamily
import com.example.ui.theme.NotoNastaliqUrduFontFamily
import com.example.ui.theme.LateefFontFamily
import com.example.ui.theme.NotoNaskhArabicFontFamily
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicAppUi(viewModel: IslamicViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val activeReciter by viewModel.activeReciter.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) } // 0: Home, 1: Quran, 2: Hadith, 3: Assistant, 4: Tools
    var activeSubScreen by remember { mutableStateOf<String?>(null) }

    if (currentUser == null) {
        AuthScreen(onLogin = { method, user ->
            viewModel.loginAs(method, user)
            Toast.makeText(context, "Welcome, physical synchronization enabled!", Toast.LENGTH_SHORT).show()
        })
    } else if (activeSubScreen != null) {
        when (activeSubScreen) {
            "SalahGuide" -> SalahGuideScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
            "Janazah" -> JanazahScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
            "AzanGuide" -> AzanGuideScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
            "PolicyCompliance" -> PolicyComplianceScreen(onBack = { activeSubScreen = null })
            "OfflineQuranBook" -> OfflineQuranBookScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    val tabs = listOf(
                        Triple("Home", Icons.Default.Home, Icons.Outlined.Home),
                        Triple("Quran", Icons.Default.MenuBook, Icons.Outlined.MenuBook),
                        Triple("Hadith", Icons.Default.AutoStories, Icons.Outlined.AutoStories),
                        Triple("AI Assistant", Icons.Default.SupportAgent, Icons.Outlined.SupportAgent),
                        Triple("Tools", Icons.Default.Widgets, Icons.Outlined.Widgets)
                    )
                    tabs.forEachIndexed { index, (label, filledIcon, outlinedIcon) ->
                        val isSelected = activeTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { activeTab = index },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) filledIcon else outlinedIcon,
                                    contentDescription = label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            alwaysShowLabel = true,
                            modifier = Modifier.testTag("nav_tab_$index")
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (activeTab) {
                    0 -> DashboardScreen(viewModel = viewModel, onTabSelected = { activeTab = it }, onNavigateToSubScreen = { activeSubScreen = it })
                    1 -> QuranScreen(viewModel = viewModel, onNavigateToSubScreen = { activeSubScreen = it })
                    2 -> HadithScreen(viewModel = viewModel)
                    3 -> AssistantScreen(viewModel = viewModel)
                    4 -> ToolsScreen(viewModel = viewModel, onNavigateToSubScreen = { activeSubScreen = it })
                }
            }
        }
    }
}

// --- LOGIN & AUTH SCREEN ---
@Composable
fun AuthScreen(onLogin: (String, String) -> Unit) {
    var phoneInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Elegant Visual Icon Ring containing Quran Book Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .drawBehind {
                        drawCircle(
                            color = Color(0xFFC5A059),
                            radius = size.minDimension / 2.3f,
                            style = Stroke(width = 4.dp.toPx())
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Quran Book Logo",
                    tint = Color(0xFFC5A059),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "القرآن الكريم",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "AL QURAN",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC5A059),
                textAlign = TextAlign.Center,
                letterSpacing = 4.sp
            )

            Text(
                text = "Your Companion for Daily Blessings & Scholarly Understanding",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Guest Pilgrim Login Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Enter Sanctuary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Enter Your Name") },
                        placeholder = { Text("e.g. Rizwan Baloch") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onLogin("GUEST", nameInput) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_guest_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Enter as Guest Pilgrim", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onLogin("ADMIN", "Admin") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_admin_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("Access Admin Panel Mode", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "OR SIGN IN SECURELY VIA", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(16.dp))

            // OAuth Simulation Options
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onLogin("GOOGLE", "Google User") },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Login, contentDescription = "Google", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Google", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onLogin("FACEBOOK", "Facebook User") },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Login, contentDescription = "Facebook", tint = Color(0xFF1877F2), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Facebook", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // OTP Phone Verification Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Mobile Phone OTP Verification", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isOtpSent) {
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Phone Number") },
                            placeholder = { Text("+92 300 1234567") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { if (phoneInput.isNotEmpty()) isOtpSent = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Send Secure OTP Code", fontSize = 12.sp)
                        }
                    } else {
                        OutlinedTextField(
                            value = codeInput,
                            onValueChange = { codeInput = it },
                            label = { Text("Enter 6-Digit OTP") },
                            placeholder = { Text("123456") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onLogin("MOBILE", "Verified Mobile Pilgrim") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Confirm Verification Code", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 0: DASHBOARD ---
@Composable
fun IslamicCrescent(
    color: Color,
    modifier: Modifier = Modifier,
    filled: Boolean = false
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sizePx = size.minDimension
        
        // Beautiful crescent path representing the Moon
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.45f, h * 0.15f)
            cubicTo(
                w * 0.85f, h * 0.2f,
                w * 0.85f, h * 0.8f,
                w * 0.45f, h * 0.85f
            )
            cubicTo(
                w * 0.67f, h * 0.72f,
                w * 0.67f, h * 0.28f,
                w * 0.45f, h * 0.15f
            )
            close()
        }
        
        if (filled) {
            drawPath(
                path = path,
                color = color.copy(alpha = 0.15f)
            )
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: IslamicViewModel, onTabSelected: (Int) -> Unit, onNavigateToSubScreen: (String) -> Unit) {
    val currentAddress by viewModel.currentAddress.collectAsStateWithLifecycle()
    val prayerTimes by viewModel.prayerTimes.collectAsStateWithLifecycle()
    val hijriDate by viewModel.hijriDate.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcoming Card Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "As-Salamu Alaykum,",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = currentUser?.name ?: "Pilgrim",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Location Indicator Button
            Surface(
                onClick = { /* trigger dialog or mock GPS change */ },
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(50.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Coordinates Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Islamabad",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- EXCLUSIVE HERO SECTION: HOLY QURAN ---
        Card(
            onClick = { onTabSelected(1) },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hero_quran_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        // Soft glow circles in background
                        drawCircle(
                            color = Color(0xFFC5A059).copy(alpha = 0.15f),
                            radius = 90.dp.toPx(),
                            center = Offset(size.width - 20.dp.toPx(), size.height / 2f)
                        )
                    }
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Surface(
                            color = Color(0xFFC5A059).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text(
                                text = "القرآن الكريم",
                                color = Color(0xFFE5C17B),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Holy Quran",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Read, listen, translate, highlight, and keep notes on verses.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Open Quran Screen",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE5C17B)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Open Quran",
                                tint = Color(0xFFE5C17B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Elegant Golden Crescent Moon in the background
                    Box(
                        modifier = Modifier
                            .weight(0.7f)
                            .height(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IslamicCrescent(
                            color = Color(0xFFE5C17B),
                            modifier = Modifier.size(100.dp),
                            filled = true
                        )
                        
                        // Icon or Quran written beautifully inside crescent
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Quran Book",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Quran",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- QUICK NAVIGATION MENU GRID ---
        Text(
            text = "Explore Sacred Features",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Hadith
            Card(
                onClick = { onTabSelected(2) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .testTag("menu_hadith")
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = "Hadith Books",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hadith Books",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "الحدیث الشریف",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Card 2: AI Assistant
            Card(
                onClick = { onTabSelected(3) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .testTag("menu_assistant")
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "AI Assistant",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AI Assistant",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "روحانی مددگار",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Card 3: Full-width Islamic Tools Suite (Premium Upgrade!)
        Card(
            onClick = { onTabSelected(4) },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("menu_tools")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Widgets,
                                    contentDescription = "Islamic Tools Suite",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Islamic Suite & Utilities",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Qibla compass, live streams, dual calendars & custom tasbeeh",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open Tools",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Beautiful scrolling chip list inside card for previewing tools!
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val toolBadges = listOf("🕌 Qibla", "📿 Tasbeeh", "📅 Calendar", "💝 Sadaqah")
                    toolBadges.forEach { badge ->
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ) {
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(
                        text = "+ More",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- NEW SACRED LEARNING GUIDES ---
        Text(
            text = "Sacred Learning Guides",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: How to Perform Salah
            Card(
                onClick = { onNavigateToSubScreen("SalahGuide") },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp)
                    .testTag("menu_salah_guide")
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Salah Guide",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Learn Salah",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Step-by-Step Tutorial",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Card 2: Janazah Prayer Guide
            Card(
                onClick = { onNavigateToSubScreen("Janazah") },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp)
                    .testTag("menu_janazah_guide")
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Janazah Guide",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Salat al-Janazah",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Four Takbeers & Duas",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Card 3: Learn Azan & Iqamah (Full Width)
        Card(
            onClick = { onNavigateToSubScreen("AzanGuide") },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .testTag("menu_azan_guide")
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Azan Guide",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Learn Azan & Iqamah",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Arabic recitation, translations, and voice lessons.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hijri Calendar Card with Dynamic Custom Day names
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        // Drawing decorative Rub el Hizb 8-pointed star in background
                        val path = androidx.compose.ui.graphics.Path()
                        val w = size.width
                        val h = size.height
                        path.moveTo(w * 0.8f, 0f)
                        path.quadraticTo(w * 0.9f, h * 0.5f, w, h * 0.3f)
                        drawPath(path, color = Color(0xFFC5A059).copy(alpha = 0.15f))
                    }
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ISLAMIC HIJRI DATE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.adjustHijriDate(-1) }) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Decrement Day", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                            Text("Moon Align", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            IconButton(onClick = { viewModel.adjustHijriDate(1) }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Increment Day", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${hijriDate.day} ${hijriDate.monthEnglish} ${hijriDate.year} AH",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Urdu: ${hijriDate.day} ${hijriDate.monthUrdu} • Arabic: ${hijriDate.dayNameArabic}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gregorian: " + SimpleDateFormat("EEEE, d MMMM yyyy", Locale.US).format(Date()),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = hijriDate.dayNameUrdu,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prayer Countdown Banner with Mosque Vector Backdrop
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .drawBehind {
                    // Draw crescent moon outline inside banner
                    drawCircle(
                        color = Color(0xFFE5C17B).copy(alpha = 0.3f),
                        radius = 45.dp.toPx(),
                        center = Offset(size.width - 50.dp.toPx(), 40.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF1B5E20), // primary masking color
                        radius = 45.dp.toPx(),
                        center = Offset(size.width - 62.dp.toPx(), 34.dp.toPx())
                    )
                }
                .padding(20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = "NEXT PRAYER ALIGNMENT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE5C17B),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Asr Prayer",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Calculated Time: ${prayerTimes.asr} • In approximately 1h 45m",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Prayer Times Timeline Row
        Text(
            text = "Today's Sacred Prayers",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val items = listOf(
                Pair("Fajr", prayerTimes.fajr),
                Pair("Sunrise", prayerTimes.sunrise),
                Pair("Dhuhr", prayerTimes.dhuhr),
                Pair("Asr", prayerTimes.asr),
                Pair("Maghrib", prayerTimes.maghrib),
                Pair("Isha", prayerTimes.isha)
            )
            items(items) { (name, time) ->
                val isActive = name == "Asr"
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = if (isActive) 1.5.dp else 0.dp,
                        color = if (isActive) MaterialTheme.colorScheme.tertiary else Color.Transparent
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.width(95.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = time,
                            fontSize = 11.sp,
                            color = if (isActive) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Daily Inspiration Sections
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Verse of the Day", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِنْ لَدُنْكَ رَحْمَةً ۚ إِنَّكَ أَنْتَ الْوَهَّابُ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"Our Lord, let not our hearts deviate after You have guided us and grant us from Yourself mercy. Indeed, You are the Bestower.\"",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Surah Ali 'Imran [3:8] • Translation: Dr. Mustafa Khattab",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoStories, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Hadith of the Day", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "إنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"Actions are judged by intentions, and every person will get what they intended.\"",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sahih al-Bukhari 1 • Narrated by Umar bin Al-Khattab (RA)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy Policy Compliance Footer Card
        Card(
            onClick = { onNavigateToSubScreen("PolicyCompliance") },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PrivacyTip,
                    contentDescription = "Privacy Policy",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Google Play Policy, Privacy & Data Safety Center",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// --- SCREEN 1: QURAN SCREEN (SURAH LIST & READER VIEW) ---
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun QuranScreen(viewModel: IslamicViewModel, onNavigateToSubScreen: (String) -> Unit = {}) {
    val selectedSurah by viewModel.selectedSurah.collectAsStateWithLifecycle()
    val ayahList by viewModel.ayahList.collectAsStateWithLifecycle()
    val isQuranLoading by viewModel.isQuranLoading.collectAsStateWithLifecycle()
    val quranBookmarks by viewModel.quranBookmarks.collectAsStateWithLifecycle()
    val activeReciter by viewModel.activeReciter.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val currentPlayingAyah by viewModel.currentPlayingAyah.collectAsStateWithLifecycle()
    val translationRecitationOption by viewModel.translationRecitationOption.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedWordForWbw by remember { mutableStateOf<QuranWord?>(null) }
    var isWbwSheetOpen by remember { mutableStateOf(false) }

    // Lifted shared layout and style states for consistency across all Quran views
    var mushafTextSize by remember { mutableStateOf(28f) } // Default 28.sp (clear, large display)
    var mushafShowTranslation by remember { mutableStateOf(false) } // Default false (Arabic self-reading)
    var mushafShowUrdu by remember { mutableStateOf(true) } // Urdu vs English translation
    var mushafFontSelected by remember { mutableStateOf("Lateef") } // Default "Lateef" (Classic Persian / Indo-Pak Quranic Style)
    var mushafLineSpacingMultiplier by remember { mutableStateOf(1.8f) } // Generous default multiplier so words never touch vertically

    val mushafFontFamily = when (mushafFontSelected) {
        "Lateef" -> LateefFontFamily
        "Amiri" -> AmiriFontFamily
        "Noto Naskh" -> NotoNaskhArabicFontFamily
        else -> QuranScheherazadeFontFamily
    }

    // Notes adding states
    var isAddNoteOpen by remember { mutableStateOf(false) }
    var noteInputText by remember { mutableStateOf("") }
    var activeAyahForNote by remember { mutableStateOf<Ayah?>(null) }

    // Color highlight picker states
    var isColorPickerOpen by remember { mutableStateOf(false) }
    var activeAyahForHighlight by remember { mutableStateOf<Ayah?>(null) }

    val activeSurah = selectedSurah

    val filteredSurahList = viewModel.surahList.filter {
        it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
        it.nameArabic.contains(searchQuery, ignoreCase = true) ||
        it.number.toString().contains(searchQuery)
    }

    if (selectedSurah == null) {
        // --- SURAH LIST VIEW ---
        var quranListTabSelected by remember { mutableStateOf(0) } // 0 = Surahs, 1 = Juz, 2 = Mushaf / Offline Read
        var mushafSelectedSurahNum by remember { mutableStateOf(1) } // Default Al-Fatiha
        var isSurahSelectorOpen by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "The Holy Quran",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Noble reading with word-by-word translations & 30 Juz offline parts",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Dynamic Resume Reading Banner based on the last bookmark!
            if (quranBookmarks.isNotEmpty()) {
                val lastBookmark = quranBookmarks.first()
                Card(
                    onClick = {
                        val surah = viewModel.surahList.firstOrNull { it.number == lastBookmark.surahNumber }
                        if (surah != null) {
                            viewModel.selectSurah(surah)
                        }
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Last Bookmarked",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "پچھلی تلاوت جاری رکھیں",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Resume: Surah ${lastBookmark.surahName} • Ayah ${lastBookmark.ayahNumber}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Button(
                            onClick = {
                                val surah = viewModel.surahList.firstOrNull { it.number == lastBookmark.surahNumber }
                                if (surah != null) {
                                    viewModel.selectSurah(surah)
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("شروع کریں", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Beautiful Tab Row for Surahs / Juz / Mushaf
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Surahs", "Juz / Parts", "Offline Quran Book").forEachIndexed { index, label ->
                    val isSelected = quranListTabSelected == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { 
                                if (index == 2) {
                                    onNavigateToSubScreen("OfflineQuranBook")
                                } else {
                                    quranListTabSelected = index
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (quranListTabSelected) {
                0 -> {
                    // --- TAB 0: SURAHS ---
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search Surah by name, number...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quran_search_bar"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredSurahList) { surah ->
                            Card(
                                onClick = { viewModel.selectSurah(surah) },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("surah_card_${surah.number}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = surah.number.toString(),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column {
                                            Text(
                                                text = surah.nameEnglish,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${surah.type} • ${surah.ayahsCount} Verses",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = surah.nameArabic,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = surah.nameTranslation,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // --- TAB 1: JUZ / PARTS ---
                    val filteredJuzList = viewModel.juzList.filter {
                        it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                        it.nameArabic.contains(searchQuery) ||
                        it.number.toString().contains(searchQuery)
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search Juz by name, number...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("juz_search_bar"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredJuzList) { juz ->
                            Card(
                                onClick = { viewModel.selectJuz(juz) },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("juz_card_${juz.number}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = juz.number.toString(),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Juz ${juz.number}",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "(${juz.nameEnglish})",
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Text(
                                                text = "Starts: Surah ${juz.startSurahName} (Ayah ${juz.startAyahNumber})",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = juz.description,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = juz.nameArabic,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "پارہ ${juz.number}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // --- TAB 2: OFFLINE QURAN BOOK ---
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Quran Book",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Offline Quran Page Book (Mushaf)",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "A beautiful page-by-page full-screen offline book view of the Holy Quran featuring custom calligraphic fonts, English & Urdu translations, dynamic bookmarking, and native offline voice recitation.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = { onNavigateToSubScreen("OfflineQuranBook") },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(0.8f).height(48.dp)
                        ) {
                            Icon(Icons.Default.Launch, contentDescription = "Launch", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Offline Quran Book", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    } else {
        // --- SURAH READER VIEW ---
        val activeSurah = selectedSurah!!
        var isDisplaySettingsExpanded by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize()) {
            // Elegant Top Surah header bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 4.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { viewModel.clearSelectedSurah() }, // Back to Surah list effectively
                        modifier = Modifier.testTag("quran_back_button")
                    ) {
                        // Quick Reset
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to List")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = activeSurah.nameEnglish,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Surah ${activeSurah.number} • ${activeSurah.ayahsCount} Ayahs",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Simple reciter dialog selection trigger
                    Box {
                        var isReciterMenuExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { isReciterMenuExpanded = true }) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Choose Reciter")
                        }
                        DropdownMenu(
                            expanded = isReciterMenuExpanded,
                            onDismissRequest = { isReciterMenuExpanded = false }
                        ) {
                            viewModel.reciters.forEach { reciter ->
                                DropdownMenuItem(
                                    text = { Text(reciter, fontSize = 12.sp) },
                                    onClick = {
                                        viewModel.selectReciter(reciter)
                                        isReciterMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Beautiful display style options to match the offline Quran perfectly
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDisplaySettingsExpanded = !isDisplaySettingsExpanded }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Display Settings",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Quran Display & Font Settings",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = if (isDisplaySettingsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isDisplaySettingsExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (isDisplaySettingsExpanded) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Font style selector
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("رسم الخط (Font style):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Row {
                                    listOf("Lateef", "Amiri", "Noto Naskh").forEach { fontName ->
                                        val isSelected = mushafFontSelected == fontName
                                        val displayName = when (fontName) {
                                            "Lateef" -> "Persian"
                                            "Amiri" -> "Uthmanic"
                                            "Noto Naskh" -> "Modern"
                                            else -> "Standard"
                                        }
                                        Text(
                                            text = displayName,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(6.dp))
                                                .clickable { mushafFontSelected = fontName }
                                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Font size adjustment
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("حجم الحرف (Font Size):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("A-", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.clickable { if (mushafTextSize > 18f) mushafTextSize -= 2f })
                                    Slider(
                                        value = mushafTextSize,
                                        onValueChange = { mushafTextSize = it },
                                        valueRange = 18f..45f,
                                        modifier = Modifier.width(90.dp)
                                    )
                                    Text("A+", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.clickable { if (mushafTextSize < 45f) mushafTextSize += 2f })
                                }
                            }

                            // Line spacing adjustment
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("فاصلہ (Line Spacing):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Row {
                                    listOf(1.6f, 1.8f, 2.0f, 2.2f).forEach { spacing ->
                                        val isSelected = mushafLineSpacingMultiplier == spacing
                                        Text(
                                            text = "${spacing}x",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(6.dp))
                                                .clickable { mushafLineSpacingMultiplier = spacing }
                                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val listState = rememberLazyListState()

            // Auto-scroll to currently playing Ayah to center it
            LaunchedEffect(currentPlayingAyah) {
                currentPlayingAyah?.let { activeNum ->
                    val targetIndex = ayahList.indexOfFirst { it.numberInSurah == activeNum }
                    if (targetIndex >= 0) {
                        try {
                            // index 0 is the Bismillah spacer, so we scroll to targetIndex + 1
                            listState.animateScrollToItem(targetIndex + 1)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            // Surah Content List
            if (isQuranLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading Authentic Verses...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Bismillah Header (except for Surah At-Tawbah)
                    if (activeSurah.number != 9) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontFamily = mushafFontFamily,
                                fontSize = (mushafTextSize + 2).sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = ((mushafTextSize + 2) * mushafLineSpacingMultiplier).sp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }
                }

                items(ayahList) { ayah ->
                    val isBookmarked = quranBookmarks.any { it.surahNumber == activeSurah.number && it.ayahNumber == ayah.numberInSurah }
                    val isPlaying = currentPlayingAyah == ayah.numberInSurah

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isPlaying) MaterialTheme.colorScheme.primary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Ayah Action Row (Play, Bookmark, Note, Highlight)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ayah ${ayah.numberInSurah}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Highlight marker
                                    IconButton(onClick = {
                                        activeAyahForHighlight = ayah
                                        isColorPickerOpen = true
                                    }) {
                                        Icon(Icons.Outlined.FormatColorFill, contentDescription = "Highlight color", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                                    }

                                    // Custom Note icon
                                    IconButton(onClick = {
                                        activeAyahForNote = ayah
                                        isAddNoteOpen = true
                                    }) {
                                        Icon(Icons.Outlined.RateReview, contentDescription = "Add Notes", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                                    }

                                    // Bookmark
                                    IconButton(onClick = { viewModel.toggleQuranBookmark(activeSurah, ayah) }) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = if (isBookmarked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Display full, continuous Arabic text and word-by-word flow starting from the right (RTL)
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Full continuous Arabic text of the Ayah
                                    Text(
                                        text = ayah.textArabic,
                                        fontFamily = mushafFontFamily,
                                        fontSize = mushafTextSize.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Start, // Since parent is RTL, Start is the right side
                                        lineHeight = (mushafTextSize * mushafLineSpacingMultiplier).sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp)
                                    )

                                    // Interactive Word-by-Word flow
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start, // Start is the right side under RTL
                                        maxItemsInEachRow = 8
                                    ) {
                                        ayah.words.forEach { word ->
                                            Box(
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .clickable {
                                                        selectedWordForWbw = word
                                                        isWbwSheetOpen = true
                                                    }
                                                    .border(
                                                        width = 1.dp,
                                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = word.arabic,
                                                    fontFamily = mushafFontFamily,
                                                    fontSize = (mushafTextSize * 0.8).sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    textAlign = TextAlign.Center,
                                                    lineHeight = (mushafTextSize * 0.8 * mushafLineSpacingMultiplier).sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Urdu and English translations
                            Text(
                                text = "Eng: ${ayah.textEnglish}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )

                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Text(
                                    text = "Urdu: ${ayah.textUrdu}",
                                    fontFamily = NotoNastaliqUrduFontFamily,
                                    fontSize = (mushafTextSize * 0.62).sp,
                                    lineHeight = (mushafTextSize * 0.62 * 1.8f).sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
            }

            // Embedded Bottom Media recitation bar
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = activeReciter,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Streaming Online Recitation Mode",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Audio controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = { viewModel.playPreviousAyah() }) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Prev Ayah", tint = MaterialTheme.colorScheme.primary)
                            }

                            FilledIconButton(
                                onClick = { viewModel.toggleAudioPlay() },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play"
                                )
                            }

                            IconButton(onClick = { viewModel.playNextAyah() }) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Next Ayah", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Voice Translation Companion",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("None", "English", "Urdu").forEach { opt ->
                                val label = when (opt) {
                                    "None" -> "Off"
                                    "English" -> "English"
                                    "Urdu" -> "Urdu"
                                    else -> ""
                                }
                                val isSelected = when (opt) {
                                    "None" -> translationRecitationOption == "None"
                                    "English" -> translationRecitationOption == "English Translation"
                                    "Urdu" -> translationRecitationOption == "Urdu Translation"
                                    else -> false
                                }
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        val newOpt = when (opt) {
                                            "None" -> "None"
                                            "English" -> "English Translation"
                                            "Urdu" -> "Urdu Translation"
                                            else -> "None"
                                        }
                                        viewModel.setTranslationRecitationOption(newOpt)
                                    },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    border = null,
                                    modifier = Modifier.height(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS & SHEET MODALS ---

    // Word by Word details dialog sheet
    if (isWbwSheetOpen && selectedWordForWbw != null) {
        val word = selectedWordForWbw!!
        Dialog(onDismissRequest = { isWbwSheetOpen = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Word-by-Word Translation",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = word.arabic,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Phonetic Pronunciation: ${word.pronunciation}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("English Mean", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(word.english, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Urdu Mean", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(word.urdu, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Audio simulation trigger button
                    Button(
                        onClick = {
                            // simulated haptic/play
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Play Pronunciation Audio")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Listen Word Audio Pronunciation", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Custom Verse Note writing sheet
    if (isAddNoteOpen && activeAyahForNote != null) {
        val verse = activeAyahForNote!!
        Dialog(onDismissRequest = { isAddNoteOpen = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Add Personal Reflection Note", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Selected Verse: ${activeSurah?.nameEnglish} Ayah ${verse.numberInSurah}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = noteInputText,
                        onValueChange = { noteInputText = it },
                        placeholder = { Text("Write notes, Tafsir summary, or self-reflections here...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { isAddNoteOpen = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            viewModel.addQuranNote(activeSurah!!, verse, noteInputText)
                            isAddNoteOpen = false
                            noteInputText = ""
                        }) {
                            Text("Save Note")
                        }
                    }
                }
            }
        }
    }

    // Verse Highlight color picker modal
    if (isColorPickerOpen && activeAyahForHighlight != null) {
        val verse = activeAyahForHighlight!!
        Dialog(onDismissRequest = { isColorPickerOpen = false }) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Highlight Verse Color", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val colors = listOf(
                            Pair("#FFE082", "Amber (Aged)"),
                            Pair("#A5D6A7", "Mint (Peace)"),
                            Pair("#90CAF9", "Aqua (Calm)"),
                            Pair("#F48FB1", "Rose (Faith)")
                        )
                        colors.forEach { (hex, name) ->
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(hex)))
                                    .clickable {
                                        viewModel.highlightVerse(activeSurah!!.number, verse.numberInSurah, hex)
                                        isColorPickerOpen = false
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 2: HADITH LIBRARY ---
@Composable
fun HadithScreen(viewModel: IslamicViewModel) {
    val selectedBook by viewModel.selectedHadithBook.collectAsStateWithLifecycle()
    val hadithList by viewModel.filteredHadithList.collectAsStateWithLifecycle()
    val hadithSearchQuery by viewModel.hadithSearchQuery.collectAsStateWithLifecycle()
    val hadithBookmarks by viewModel.hadithBookmarks.collectAsStateWithLifecycle()
    val currentPlayingHadithNumber by viewModel.currentPlayingHadithNumber.collectAsStateWithLifecycle()
    val hadithTtsOption by viewModel.hadithTtsOption.collectAsStateWithLifecycle()

    if (selectedBook == null) {
        // --- HADITH BOOKS GRID DISPLAY ---
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Authentic Hadith Library",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "The 8 major canonical collections of Sunnah in Arabic, English, and Urdu",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(viewModel.hadithBooks) { book ->
                    Card(
                        onClick = { viewModel.selectHadithBook(book) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .testTag("hadith_book_${book.key}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AutoStories, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = book.rating,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }

                            Column {
                                Text(
                                    text = book.nameEnglish,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = book.nameArabic,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Author: ${book.author}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // --- DETAILED HADITH LIST WITHIN SELECTED BOOK ---
        val book = selectedBook!!

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.selectHadithBook(book) }) { // effectively toggle list back
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Books")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(book.nameEnglish, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                            Text("Sunnah Authority Rating: ${book.rating}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = hadithSearchQuery,
                        onValueChange = { viewModel.setHadithSearchQuery(it) },
                        placeholder = { Text("Search by narration keyword, number...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hadith_inner_search"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Voice Translation Companion",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Arabic Only", "Arabic + Urdu", "Arabic + English").forEach { opt ->
                            val label = when (opt) {
                                "Arabic Only" -> "Arabic Only"
                                "Arabic + Urdu" -> "Arabic + Urdu"
                                "Arabic + English" -> "Arabic + English"
                                else -> opt
                            }
                            val isSelected = hadithTtsOption == opt
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setHadithTtsOption(opt) },
                                label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = null,
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(hadithList) { hadith ->
                    val isBookmarked = hadithBookmarks.any { it.bookName == book.nameEnglish && it.hadithNumber == hadith.number }

                    val isPlaying = currentPlayingHadithNumber == hadith.number

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isPlaying) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = hadith.number,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = hadith.chapter,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.playHadith(hadith) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                            contentDescription = if (isPlaying) "Stop Voice" else "Play Hadith and translation voice",
                                            tint = if (isPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(onClick = { viewModel.toggleHadithBookmark(book, hadith) }) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = if (isBookmarked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = hadith.textArabic,
                                fontFamily = LateefFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Right,
                                lineHeight = 44.sp,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Narrated by: ${hadith.narrator}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Eng: ${hadith.textEnglish}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Text(
                                    text = "Urdu: ${hadith.textUrdu}",
                                    fontFamily = NotoNastaliqUrduFontFamily,
                                    fontSize = 17.sp,
                                    lineHeight = 32.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 3: AI SCHOLARLY ASSISTANT (GEMINI CHAT) ---
@Composable
fun AssistantScreen(viewModel: IslamicViewModel) {
    val chatMessages by viewModel.aiChatMessages.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    var inputPrompt by remember { mutableStateOf("") }

    val context = LocalContext.current
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrEmpty()) {
                inputPrompt = spokenText
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI Islamic Assistant",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Asks references from holy scriptures securely",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { viewModel.clearAiChat() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Clear Chat Conversation")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Preset Prompt Chips
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val prompts = listOf(
                "Importance of Tahajjud Prayer?",
                "Hadith on sincerity (Niyyah)",
                "Patience (Sabr) in Quran",
                "Ethics of trade in Islam"
            )
            prompts.forEach { prompt ->
                FilterChip(
                    selected = false,
                    onClick = { viewModel.askAiAssistant(prompt) },
                    label = { Text(prompt, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chats Thread
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { msg ->
                val align = if (msg.isUser) Alignment.End else Alignment.Start
                val bg = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                val textCol = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurface

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                    bottomEnd = if (msg.isUser) 4.dp else 16.dp
                                )
                            )
                            .background(bg)
                            .padding(14.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.text,
                            fontSize = 13.sp,
                            color = textCol
                        )
                    }
                    Text(
                        text = if (msg.isUser) "You" else "Scholarly Companion",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                    )
                }
            }

            if (isAiLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Scholar is studying references...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputPrompt,
                onValueChange = { inputPrompt = it },
                placeholder = { Text("Ask about Quran, Hadith, ethics...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("assistant_chat_input"),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ur-PK")
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "بولیں (Speak now...)")
                            }
                            try {
                                speechRecognizerLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Voice input is not supported on this device!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (inputPrompt.isNotEmpty()) {
                        viewModel.askAiAssistant(inputPrompt)
                        inputPrompt = ""
                    }
                })
            )

            FloatingActionButton(
                onClick = {
                    if (inputPrompt.isNotEmpty()) {
                        viewModel.askAiAssistant(inputPrompt)
                        inputPrompt = ""
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("assistant_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

// --- SCREEN 4: ADDITIONAL ISLAMIC TOOLS (GRID SELECTION) ---
@Composable
fun ToolsScreen(viewModel: IslamicViewModel, onNavigateToSubScreen: (String) -> Unit) {
    var selectedTool by remember { mutableStateOf<String?>(null) }

    if (selectedTool == null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Islamic Companionship Suite",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            val tools = listOf(
                Pair("Offline Quran (Page Book)", Icons.Default.MenuBook),
                Pair("How to Perform Salah", Icons.Default.PlayCircle),
                Pair("Janazah Prayer Guide", Icons.Default.Group),
                Pair("Learn Azan (Call to Prayer)", Icons.Default.NotificationsActive),
                Pair("Policy & Compliance", Icons.Default.PrivacyTip),
                Pair("Qibla Compass", Icons.Default.Explore),
                Pair("Virtual Tasbeeh", Icons.Default.Dialpad),
                Pair("Islamic Calendar", Icons.Default.CalendarMonth),
                Pair("Daily Supplications", Icons.Default.SettingsAccessibility),
                Pair("Alarms & Reminders", Icons.Default.Alarm),
                Pair("99 Names of Allah", Icons.Default.AutoAwesome),
                Pair("Kaaba Live Stream", Icons.Default.LiveTv),
                Pair("Nearby Mosques", Icons.Default.HomeWork),
                Pair("Identity & Backup", Icons.Default.CloudSync),
                Pair("Admin Control", Icons.Default.AdminPanelSettings),
                Pair("App Installer QR", Icons.Default.QrCode),
                Pair("App Promotion Flyer", Icons.Default.Campaign),
                Pair("Sadaqah & Donations", Icons.Default.Favorite)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tools) { (name, icon) ->
                    Card(
                        onClick = {
                            if (name == "Offline Quran (Page Book)") {
                                onNavigateToSubScreen("OfflineQuranBook")
                            } else if (name == "How to Perform Salah") {
                                onNavigateToSubScreen("SalahGuide")
                            } else if (name == "Janazah Prayer Guide") {
                                onNavigateToSubScreen("Janazah")
                            } else if (name == "Learn Azan (Call to Prayer)") {
                                onNavigateToSubScreen("AzanGuide")
                            } else if (name == "Policy & Compliance") {
                                onNavigateToSubScreen("PolicyCompliance")
                            } else {
                                selectedTool = name
                            }
                        },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("tool_card_${name.replace(" ", "_")}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = icon, contentDescription = name, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header for active tool
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedTool = null }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back to Grid")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedTool!!, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            }

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (selectedTool) {
                    "Qibla Compass" -> QiblaCompassTool(viewModel = viewModel)
                    "Virtual Tasbeeh" -> TasbeehTool(viewModel = viewModel)
                    "Islamic Calendar" -> CalendarTool(viewModel = viewModel)
                    "Daily Supplications" -> SupplicationsTool(viewModel = viewModel)
                    "Alarms & Reminders" -> AlarmsTool(viewModel = viewModel)
                    "99 Names of Allah" -> Names99Tool(viewModel = viewModel)
                    "Kaaba Live Stream" -> LiveMakkahTool(viewModel = viewModel)
                    "Nearby Mosques" -> MosquesTool(viewModel = viewModel)
                    "Identity & Backup" -> CloudBackupTool(viewModel = viewModel)
                    "Admin Control" -> AdminPanelTool(viewModel = viewModel)
                    "App Installer QR" -> AppInstallerQrTool(viewModel = viewModel)
                    "App Promotion Flyer" -> AppPromoTool(viewModel = viewModel)
                    "Sadaqah & Donations" -> DonationTool(viewModel = viewModel)
                }
            }
        }
    }
}

// --- SUB TOOL 1: QIBLA COMPASS ---
@Composable
fun QiblaCompassTool(viewModel: IslamicViewModel) {
    val qiblaAngle by viewModel.qiblaAngle.collectAsStateWithLifecycle()
    var simulatedSensorRotation by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Orient your device until the needle aligns with the Gold Marker", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))

        // Fully Dynamic Animated Compass Dial utilizing overlapping layers and drag-to-simulate interaction
        Box(
            modifier = Modifier
                .size(240.dp)
                .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                .clickable {
                    // Tap to simulate dynamic rotation movement
                    simulatedSensorRotation = (0..359).random().toFloat()
                },
            contentAlignment = Alignment.Center
        ) {
            // Rotated outer dial
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(-simulatedSensorRotation)
            ) {
                // North indicator
                Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.TopCenter) {
                    Text("N", fontWeight = FontWeight.Black, color = Color.Red, fontSize = 16.sp)
                }
                // Kaaba Gold Icon position based on calculate qiblaAngle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(qiblaAngle.toFloat()),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Icon(Icons.Default.Place, contentDescription = "Kaaba Angle Alignment", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(24.dp))
                }
            }

            // Real-time Compass needle
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Compass Needle",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(64.dp)
                    .rotate(qiblaAngle.toFloat() - simulatedSensorRotation)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Calculated Qibla angle: ${qiblaAngle.toInt()}° NNE",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            // Trigger randomized sensor update simulation
            simulatedSensorRotation = (0..359).random().toFloat()
        }) {
            Text("Simulate Compass Sensor Alignment Rotate")
        }
    }
}

// --- SUB TOOL 2: VIRTUAL TASBEEH ---
@Composable
fun TasbeehTool(viewModel: IslamicViewModel) {
    val currentPhrase by viewModel.currentTasbeehPhrase.collectAsStateWithLifecycle()
    val count by viewModel.tasbeehCount.collectAsStateWithLifecycle()
    val target by viewModel.tasbeehTarget.collectAsStateWithLifecycle()
    val history by viewModel.tasbeehHistory.collectAsStateWithLifecycle()

    var isDropdownOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Selection Phrase button
        Box {
            Button(onClick = { isDropdownOpen = true }, shape = RoundedCornerShape(12.dp)) {
                Text(currentPhrase)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = isDropdownOpen, onDismissRequest = { isDropdownOpen = false }) {
                viewModel.tasbeehPhrases.forEach { phrase ->
                    DropdownMenuItem(
                        text = { Text(phrase) },
                        onClick = {
                            viewModel.selectTasbeehPhrase(phrase)
                            isDropdownOpen = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target chips selection
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(33, 34, 100).forEach { t ->
                FilterChip(
                    selected = target == t,
                    onClick = { viewModel.adjustTasbeehTarget(t) },
                    label = { Text("Target: $t") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Giant glowing counter button
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
                .clickable {
                    viewModel.incrementTasbeeh()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$count / $target",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "TAP BEAD",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { viewModel.resetCurrentTasbeeh() }) {
                Text("Reset Counter")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

        Spacer(modifier = Modifier.height(16.dp))

        // History completions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tasbeeh Completion Records", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            TextButton(onClick = { viewModel.clearTasbeehHistory() }) {
                Text("Clear Log")
            }
        }

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No completions logged yet. Complete a target to save!", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                history.forEach { item ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Achieved: ${item.currentCount} / ${item.targetCount} beads", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

// --- SUB TOOL 3: ISLAMIC CALENDAR ---
@Composable
fun CalendarTool(viewModel: IslamicViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Hijri Month Holy Events Calendar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))

        viewModel.eventsList.forEach { event ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(event.nameEnglish, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Urdu: ${event.nameUrdu}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(event.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = event.dateHijri,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- SUB TOOL 4: DAILY SUPPLICATIONS ---
@Composable
fun SupplicationsTool(viewModel: IslamicViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        viewModel.duasList.forEach { dua ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(dua.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Text("Category: ${dua.category} • Ref: ${dua.reference}", fontSize = 10.sp, color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dua.textArabic,
                        fontFamily = LateefFontFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Right,
                        lineHeight = 44.sp,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Eng: ${dua.translationEnglish}", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = "Urdu: ${dua.translationUrdu}",
                            fontFamily = NotoNastaliqUrduFontFamily,
                            fontSize = 17.sp,
                            lineHeight = 32.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- SUB TOOL 5: ALARMS & ISLAMIC REMINDERS ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsTool(viewModel: IslamicViewModel) {
    val alarms by viewModel.islamicAlarms.collectAsStateWithLifecycle()
    val isReminderEnabled by viewModel.isReminderEnabled.collectAsStateWithLifecycle()
    val reminderHour by viewModel.reminderHour.collectAsStateWithLifecycle()
    val reminderMinute by viewModel.reminderMinute.collectAsStateWithLifecycle()
    
    val context = LocalContext.current

    var showAddAlarmDialog by remember { mutableStateOf(false) }
    var alarmName by remember { mutableStateOf("") }
    var alarmTime by remember { mutableStateOf("") }
    var alarmType by remember { mutableStateOf("CUSTOM") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- DAILY QURAN & HADITH REMINDER PANEL ---
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            ),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Daily Reminder",
                            tint = Color(0xFFC5A059),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "روزانہ کی یاد دہانی",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Daily Quran & Hadith Reminder",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Switch(
                        checked = isReminderEnabled,
                        onCheckedChange = { 
                            viewModel.setReminderEnabled(it)
                            val statusMsg = if (it) "روزانہ کی یاد دہانی فعال ہو گئی!" else "روزانہ کی یاد دہانی غیر فعال ہو گئی!"
                            Toast.makeText(context, statusMsg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ہر روز آپ کو ایک منتخب قرآنی آیت یا مستند حدیث مبارکہ کا اردو اور انگلش ترجمہ کے ساتھ نوٹیفکیشن موصول ہوگا، تاکہ آپ ایپ کھولے بغیر بھی دین سے جڑے رہیں۔",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "نوٹیفکیشن کا وقت (Notification Time):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Time Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isMorning = reminderHour == 8 && reminderMinute == 0
                    val isEvening = reminderHour == 21 && reminderMinute == 0
                    
                    Button(
                        onClick = { 
                            viewModel.updateReminderTime(8, 0)
                            Toast.makeText(context, "وقت تبدیل کر کے صبح 8:00 بجے کر دیا گیا ہے!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMorning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isMorning) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🌅 صبح 8:00 بجے", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { 
                            viewModel.updateReminderTime(21, 0)
                            Toast.makeText(context, "وقت تبدیل کر کے رات 9:00 بجے کر دیا گیا ہے!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEvening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isEvening) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🌙 رات 9:00 بجے", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Custom Time Input
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = String.format("موجودہ وقت: %02d:%02d %s", 
                                if (reminderHour % 12 == 0) 12 else reminderHour % 12, 
                                reminderMinute, 
                                if (reminderHour >= 12) "PM" else "AM"
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("گھنٹہ: ", fontSize = 11.sp)
                            IconButton(
                                onClick = { 
                                    val newHour = if (reminderHour > 0) reminderHour - 1 else 23
                                    viewModel.updateReminderTime(newHour, reminderMinute)
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease hour", modifier = Modifier.size(16.dp))
                            }
                            Text(String.format("%02d", reminderHour), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            IconButton(
                                onClick = { 
                                    val newHour = if (reminderHour < 23) reminderHour + 1 else 0
                                    viewModel.updateReminderTime(newHour, reminderMinute)
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase hour", modifier = Modifier.size(16.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text("منٹ: ", fontSize = 11.sp)
                            IconButton(
                                onClick = { 
                                    val newMin = if (reminderMinute >= 5) reminderMinute - 5 else 55
                                    viewModel.updateReminderTime(reminderHour, newMin)
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease minute", modifier = Modifier.size(16.dp))
                            }
                            Text(String.format("%02d", reminderMinute), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            IconButton(
                                onClick = { 
                                    val newMin = if (reminderMinute <= 50) reminderMinute + 5 else 0
                                    viewModel.updateReminderTime(reminderHour, newMin)
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase minute", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Test Button
                Button(
                    onClick = { 
                        viewModel.triggerTestNotification()
                        Toast.makeText(context, "ٹیسٹ نوٹیفکیشن بھیج دیا گیا ہے! Test notification sent!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC5A059)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = "Test Notification")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ابھی ٹیسٹ نوٹیفکیشن حاصل کریں", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- CUSTOM ALARMS & PRAYER REMINDERS SECTION ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Active Prayer & Custom Alarms", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Button(onClick = { showAddAlarmDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Alarm")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (alarms.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No custom prayer alarms set yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                alarms.forEach { alarm ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(alarm.label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Trigger Scheduled: ${alarm.timeString} • Type: ${alarm.alarmType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { viewModel.deleteIslamicAlarm(alarm.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showAddAlarmDialog) {
        Dialog(onDismissRequest = { showAddAlarmDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Create Custom Reminder", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = alarmName,
                        onValueChange = { alarmName = it },
                        label = { Text("Reminder Label") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = alarmTime,
                        onValueChange = { alarmTime = it },
                        label = { Text("Scheduled Time (e.g. 05:15 AM)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddAlarmDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (alarmName.isNotEmpty() && alarmTime.isNotEmpty()) {
                                viewModel.addIslamicAlarm(alarmName, alarmType, alarmTime)
                                showAddAlarmDialog = false
                                alarmName = ""
                                alarmTime = ""
                            }
                        }) {
                            Text("Schedule")
                        }
                    }
                }
            }
        }
    }
}

// --- SUB TOOL 6: 99 NAMES OF ALLAH ---
@Composable
fun Names99Tool(viewModel: IslamicViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(viewModel.Names99) { name ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(name.arabic, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(name.transliteration, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                    Text(name.translationEnglish, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                    Text("Urdu: ${name.translationUrdu}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(name.benefits, fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center, lineHeight = 11.sp)
                }
            }
        }
    }
}

// --- SUB TOOL 7: KAABA LIVE STREAM SECTION ---
@Composable
fun LiveMakkahTool(viewModel: IslamicViewModel) {
    val liveTime by viewModel.makkahLiveTime.collectAsStateWithLifecycle()
    val viewers by viewModel.liveViewerCount.collectAsStateWithLifecycle()
    val activeStream by viewModel.liveActiveStream.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mock stream player box using premium gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.LiveTv, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("KAABA LIVE FEED 4K", fontWeight = FontWeight.Bold, color = Color.White)
                Text("Realtime Sync Active", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
            }

            Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.TopStart) {
                Surface(color = Color.Red, shape = RoundedCornerShape(4.dp)) {
                    Text("LIVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.BottomEnd) {
                Text("Makkah Time: $liveTime", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Simulated Streams Selection", fontWeight = FontWeight.Bold)
            Text("Viewers: $viewers", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        viewModel.liveStreams.forEach { stream ->
            val isSelected = activeStream == stream
            Card(
                onClick = { viewModel.selectLiveStream(stream) },
                colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(stream, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

// --- SUB TOOL 8: NEARBY MOSQUES ---
@Composable
fun MosquesTool(viewModel: IslamicViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Mosques Around Your Location", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))

        viewModel.mosquesList.forEach { mosque ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(mosque.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(mosque.address, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Rating: ★ ${mosque.rating}", fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                    }
                    Text(mosque.distance, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
            }
        }
    }
}

// --- SUB TOOL 9: IDENTITY & CLOUD SYNC ---
@Composable
fun CloudBackupTool(viewModel: IslamicViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Online SQLite Sync Active", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Every bookmark, reflection note, custom prayer alarm, and Tasbeeh completion history is automatically sync'd to your profile securely in real-time.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Active Session Information", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Identity Method: ${currentUser?.authProvider}", fontSize = 12.sp)
                Text("Display Name: ${currentUser?.name}", fontSize = 12.sp)
                Text("Registered Email: ${currentUser?.email}", fontSize = 12.sp)
                Text("Identity System Privilege: ${currentUser?.role}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { viewModel.logout() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
            Text("Logout & Reset Session", color = Color.White)
        }
    }
}

// --- SUB TOOL 10: ADMIN PANEL CONTROLS ---
@Composable
fun AdminPanelTool(viewModel: IslamicViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val notificationText by viewModel.adminNotificationText.collectAsStateWithLifecycle()
    val logs by viewModel.adminSystemLogs.collectAsStateWithLifecycle()

    var customTitle by remember { mutableStateOf("") }
    var customBody by remember { mutableStateOf("") }

    if (currentUser?.role != "ADMIN") {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(
                "Access Denied: You must sign in with Admin privilege mode from the entrance screen to use the Content Management Panel.",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Admin Central Notification Broadcaster", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = customTitle,
                onValueChange = { customTitle = it },
                label = { Text("Global Notification Title") },
                placeholder = { Text("e.g., Ramadan Kareem Broadcast") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customBody,
                onValueChange = { customBody = it },
                label = { Text("Global Notification Body") },
                placeholder = { Text("Write global alert message for all pilgrims...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (customTitle.isNotEmpty() && customBody.isNotEmpty()) {
                        viewModel.triggerGlobalAdminNotification(customTitle, customBody)
                        customTitle = ""
                        customBody = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Broadcast Global Alert System")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            Spacer(modifier = Modifier.height(16.dp))

            Text("Broadcasting System Events Logs", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                logs.forEach { log ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))) {
                        Text(
                            text = log,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QrCodeView(
    content: String,
    modifier: Modifier = Modifier,
    qrColor: Color = Color.Black,
    backgroundColor: Color = Color.White
) {
    val bitMatrix = remember(content) {
        try {
            MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 350, 350)
        } catch (e: Exception) {
            null
        }
    }

    if (bitMatrix != null) {
        Canvas(modifier = modifier) {
            val width = bitMatrix.width
            val height = bitMatrix.height
            val cellWidth = size.width / width
            val cellHeight = size.height / height

            // Draw background
            drawRect(color = backgroundColor)

            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix.get(x, y)) {
                        drawRect(
                            color = qrColor,
                            topLeft = Offset(x * cellWidth, y * cellHeight),
                            size = androidx.compose.ui.geometry.Size(cellWidth + 0.4f, cellHeight + 0.4f)
                        )
                    }
                }
            }
        }
    } else {
        Box(
            modifier = modifier.background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text("Error generating QR code", color = Color.Red, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInstallerQrTool(viewModel: IslamicViewModel) {
    val context = LocalContext.current
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    var urlText by remember { mutableStateOf("https://ais-pre-qc2o774rlxwue2lccovmus-601033371983.europe-west2.run.app") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome and intro card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scan icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Instant Mobile Installation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Scan the QR code to download and test this application on your physical Android phone.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // High contrast QR card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .size(260.dp)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                QrCodeView(
                    content = urlText,
                    modifier = Modifier.fillMaxSize(),
                    qrColor = Color(0xFF1B5E20), // Premium Islamic Green for QR code blocks
                    backgroundColor = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Editable download URL Field
        Text(
            text = "Target Installation URL",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = urlText,
            onValueChange = { urlText = it },
            placeholder = { Text("https://...") },
            leadingIcon = { Icon(Icons.Default.Link, contentDescription = "URL Link") },
            trailingIcon = {
                IconButton(onClick = {
                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = android.content.ClipData.newPlainText("Installation URL", urlText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "URL Copied to clipboard!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_install_url_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                try {
                    uriHandler.openUri(urlText)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open link: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("app_install_direct_download_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Download, contentDescription = "Download Direct")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download APK / Open in Browser", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle presets
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val presets = listOf(
                Pair("Shared Web App", "https://ais-pre-qc2o774rlxwue2lccovmus-601033371983.europe-west2.run.app"),
                Pair("Direct APK Link", "https://ais-pre-qc2o774rlxwue2lccovmus-601033371983.europe-west2.run.app/apk")
            )
            presets.forEach { (label, url) ->
                val isSelected = urlText == url
                ElevatedFilterChip(
                    selected = isSelected,
                    onClick = { urlText = url },
                    label = { Text(label, fontSize = 11.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Step-by-Step Installation Guide
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "How to Install on Mobile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val steps = listOf(
                    Triple("1", "Scan QR Code", "Open your camera app or a QR scanner and focus on the QR block above."),
                    Triple("2", "Allow Unknown Sources", "To install outside Google Play, enable \"Install Unknown Apps\" for your browser (Chrome) if asked."),
                    Triple("3", "Download and Open APK", "Press the download button, wait for the file to finish, then tap open."),
                    Triple("4", "Complete Installation", "Press \"Install\" on the system dialog. Locate the application icon on your home screen.")
                )

                steps.forEachIndexed { index, (num, title, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = num,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (index < steps.size - 1) {
                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            modifier = Modifier.padding(start = 36.dp, top = 4.dp, bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- SUB TOOL 12: SADAQAH & DONATIONS ---
@Composable
fun DonationTool(viewModel: IslamicViewModel) {
    val context = LocalContext.current
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    
    val accountName by viewModel.donationAccountName.collectAsStateWithLifecycle()
    val phoneNumber by viewModel.donationPhoneNumber.collectAsStateWithLifecycle()
    val bankName by viewModel.donationBankName.collectAsStateWithLifecycle()
    val accountNumber by viewModel.donationAccountNumber.collectAsStateWithLifecycle()
    val donationLink by viewModel.donationLink.collectAsStateWithLifecycle()
    
    var currentSubTab by remember { mutableStateOf(0) } // 0: Donate Mode, 1: Settings Mode
    
    var nameInput by remember { mutableStateOf(accountName) }
    var phoneInput by remember { mutableStateOf(phoneNumber) }
    var bankInput by remember { mutableStateOf(bankName) }
    var accountInput by remember { mutableStateOf(accountNumber) }
    var linkInput by remember { mutableStateOf(donationLink) }
    
    // Sync local state when viewmodel updates
    LaunchedEffect(accountName, phoneNumber, bankName, accountNumber, donationLink) {
        nameInput = accountName
        phoneInput = phoneNumber
        bankInput = bankName
        accountInput = accountNumber
        linkInput = donationLink
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Urdu / English beautiful sub-tabs
        TabRow(
            selectedTabIndex = currentSubTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = currentSubTab == 0,
                onClick = { currentSubTab = 0 },
                text = { Text("عطیہ بھیجیں (Donate)", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = currentSubTab == 1,
                onClick = { currentSubTab = 1 },
                text = { Text("سیٹنگز (Settings)", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (currentSubTab == 0) {
            // DONATE MODE (عطیہ دہندہ موڈ)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Sadaqah",
                        tint = Color(0xFFC5A059),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "صدقہ اور عطیات (Sadaqah & Donations)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "آپ کا صدقہ ضرورت مندوں کے لیے خوشی اور آسانی کا باعث بنتا ہے۔ برائے مہربانی درج ذیل تفصیلات کے ذریعے اپنا ہدیہ ارسال کریں۔",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "اکاؤنٹ کی تفصیلات (Payment Details):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Render beautiful fields with quick copy
            val copyData = listOf(
                Triple("اکاؤنٹ ہولڈر کا نام (Account Title)", accountName, "Account Title"),
                Triple("بینک کا نام (Bank Name)", bankName, "Bank Name"),
                Triple("بینک اکاؤنٹ نمبر (Account Number)", accountNumber.ifEmpty { "Not set" }, "Account Number"),
                Triple("ایزی پیسہ / موبائل نمبر (EasyPaisa / Phone)", phoneNumber.ifEmpty { "Not set" }, "Phone Number")
            )
            
            copyData.forEach { (label, value, clipLabel) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = value,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        if (value.isNotEmpty() && value != "Not set") {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText(clipLabel, value)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "$clipLabel copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy $clipLabel",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            if (donationLink.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        try {
                            uriHandler.openUri(donationLink)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Invalid link URL format!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC5A059)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Link, contentDescription = "Web Link")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("آن لائن عطیہ کریں (Donate Online Now)", fontWeight = FontWeight.Bold)
                }
            }
            
        } else {
            // SETTINGS MODE (معلومات میں ترمیم)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "تفصیلات درج کریں (Configure Details)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("اکاؤنٹ ہولڈر کا نام (Account Title)") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = bankInput,
                        onValueChange = { bankInput = it },
                        label = { Text("بینک کا نام (Bank Name)") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = accountInput,
                        onValueChange = { accountInput = it },
                        label = { Text("بینک اکاؤنٹ نمبر (Account Number)") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("ایزی پیسہ / جاز کیش نمبر (EasyPaisa/Phone)") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = linkInput,
                        onValueChange = { linkInput = it },
                        label = { Text("عطیہ کا آن لائن لنک (Donation Link)") },
                        placeholder = { Text("https://example.com/donate") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            viewModel.updateDonationDetails(
                                name = nameInput,
                                phone = phoneInput,
                                bank = bankInput,
                                accNum = accountInput,
                                link = linkInput
                            )
                            Toast.makeText(context, "تفصیلات کامیابی سے محفوظ ہو گئیں! Details Saved!", Toast.LENGTH_LONG).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Details")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تفصیلات محفوظ کریں (Save Details)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AppPromoTool(viewModel: IslamicViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📢 Premium App Promotion Kit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "A professionally generated promotional banner showcasing the visual elegance and major capabilities of this modern Islamic companion app.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Premium Promo Banner Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Load local drawable banner
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_promo_1782905083810),
                    contentDescription = "App Promotion Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Al Quran & Hadith Companion",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Visualizing and reciting the words of Allah and His Messenger with pristine dynamic beauty.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Key Selling Features Showcase:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val bullets = listOf(
                        "📖 Authentic Quran Reader: Dynamic online streaming of 3 legendary reciters, complete word-by-word breakdowns, bookmarks, highlights, and custom notes.",
                        "🎙️ Hadith Audio Engine: Complete TTS audio recitation for authentic Bukhari & Muslim Hadiths, with synchronized translation speech in Urdu & English.",
                        "🕌 Full Islamic Suite: High-precision Qibla Compass, custom Tasbeeh counter, Hijri calendar events, and live Kaaba/Makkah stream.",
                        "📱 Premium Adaptive UI: Dynamic edge-to-edge screens with gorgeous dark twilight color systems, optimized for both phones and tablets."
                    )

                    bullets.forEach { bullet ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = bullet, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Copy Share Text Button
        Button(
            onClick = {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val shareText = """
                    🌟 Al Quran & Hadith - The Ultimate Islamic Companion App! 🌟
                    
                    📖 Authentic Quran Reader with dynamic streaming recitations.
                    🎙️ Hadith Audio recitations with Urdu & English voice translation.
                    🕌 Full Suite: Qibla Compass, Tasbeeh counter, 99 Names of Allah benefits, Kaaba stream, and much more!
                    
                    Download and test now: https://ais-pre-qc2o774rlxwue2lccovmus-601033371983.europe-west2.run.app
                """.trimIndent()
                val clip = android.content.ClipData.newPlainText("App Promo Pitch", shareText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Promotion message copied to clipboard! Share it with friends and family!", Toast.LENGTH_LONG).show()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Share, contentDescription = "Copy Promotion Pitch")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Copy Promotion Pitch & Link", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
