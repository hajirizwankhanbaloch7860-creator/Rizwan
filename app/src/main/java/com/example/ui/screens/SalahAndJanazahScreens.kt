package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.IslamicViewModel
import com.example.ui.theme.LateefFontFamily
import com.example.ui.theme.NotoNastaliqUrduFontFamily
import com.example.ui.theme.QuranScheherazadeFontFamily

// --- DATA STRUCTURES ---

data class SalahStep(
    val title: String,
    val arabicPhrase: String = "",
    val translation: String = "",
    val description: String,
    val audioText: String,
    val pose: String // "TAKBEER", "QIYAM", "RUKU", "SUJOOD", "JALSA", "TASHAHHUD", "SALAM"
)

// --- MAIN SALAH GUIDE SCREEN ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalahGuideScreen(viewModel: IslamicViewModel, onBack: () -> Unit) {
    var selectedPrayer by remember { mutableStateOf("Fajr") }
    val isSpeaking by viewModel.isTtsSpeaking.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val prayerSteps = remember(selectedPrayer) {
        getStepsForPrayer(selectedPrayer)
    }

    var currentStepIndex by remember(selectedPrayer) { mutableStateOf(0) }
    val currentStep = prayerSteps[currentStepIndex]

    // Lifted state for adjustable clearly visible letters
    var arabicFontSize by remember { mutableStateOf(32f) }

    // Reset speaker when step or prayer changes
    LaunchedEffect(currentStepIndex, selectedPrayer) {
        viewModel.stopSpeaking()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Learn How to Perform Salah",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Prayer Selection Row (Fajr, Dhuhr, Asr, Maghrib, Isha)
        val prayers = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
        ScrollableTabRow(
            selectedTabIndex = prayers.indexOf(selectedPrayer),
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            prayers.forEach { prayer ->
                Tab(
                    selected = selectedPrayer == prayer,
                    onClick = { selectedPrayer = prayer },
                    text = {
                        Text(
                            text = prayer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        // Adjustable clearly visible letters slider
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = "Text Size Control",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Arabic Letter Size",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "A-",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable { if (arabicFontSize > 22f) arabicFontSize -= 2f }
                            .padding(4.dp)
                    )
                    Slider(
                        value = arabicFontSize,
                        onValueChange = { arabicFontSize = it },
                        valueRange = 22f..48f,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = "A+",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clickable { if (arabicFontSize < 48f) arabicFontSize += 2f }
                            .padding(4.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Feature Banner Visual
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.img_salah_banner_1783012997082),
                            contentDescription = "Salah Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f))
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Step-by-Step $selectedPrayer Tutorial",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Perfect your postures, recitation, and timings.",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Interactive 3D Posture Demonstrator
            item {
                Text(
                    text = "3D Posture Demonstration",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing the 3D fluid posture
                        SalahPostureCanvas(
                            pose = currentStep.pose,
                            modifier = Modifier
                                .size(200.dp)
                                .padding(16.dp)
                        )

                        // Mode indicator tag
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = currentStep.pose,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Current Step Instructions Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Step ${currentStepIndex + 1} of ${prayerSteps.size}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentStep.title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (currentStep.arabicPhrase.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = currentStep.arabicPhrase,
                                fontFamily = LateefFontFamily,
                                fontSize = arabicFontSize.sp,
                                color = Color(0xFF1B4D3E),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (currentStep.translation.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentStep.translation,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentStep.description,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Audio Recitation & Guidance Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (currentStep.arabicPhrase.isNotEmpty()) {
                                Button(
                                    onClick = {
                                        if (isSpeaking) {
                                            viewModel.stopSpeaking()
                                        } else {
                                            viewModel.speakText(currentStep.arabicPhrase, "ar")
                                            Toast.makeText(context, "Playing Arabic recitation...", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(42.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Listen Arabic",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Listen Arabic", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = {
                                    if (isSpeaking) {
                                        viewModel.stopSpeaking()
                                    } else {
                                        viewModel.speakText(currentStep.audioText, "en")
                                        Toast.makeText(context, "Playing audio guide...", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = "Listen Guide",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Listen Guide", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Navigation Controls (Previous, Next)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (currentStepIndex > 0) {
                                currentStepIndex--
                            }
                        },
                        enabled = currentStepIndex > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Prev", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            if (currentStepIndex < prayerSteps.size - 1) {
                                currentStepIndex++
                            } else {
                                Toast.makeText(context, "MashaAllah! Tutorial Completed.", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(if (currentStepIndex == prayerSteps.size - 1) "Finish" else "Next Step", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        if (currentStepIndex < prayerSteps.size - 1) {
                            Icon(Icons.Default.ArrowForwardIos, contentDescription = "Next", modifier = Modifier.size(16.dp))
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "Done", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- 3D INTERACTIVE SKELETAL CANVAS DRAWING ENGINE ---

@Composable
fun SalahPostureCanvas(pose: String, modifier: Modifier = Modifier) {
    // Dynamic Spring Animations for each joint coordinate to achieve a beautiful, fluid transition!
    val transitionSpec = spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium)

    // Base coordinate animations facing left
    val headX = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 140f
            "QIYAM" -> 140f
            "RUKU" -> 80f
            "SUJOOD" -> 60f
            "JALSA", "TASHAHHUD", "SALAM" -> 120f
            else -> 140f
        }, animationSpec = transitionSpec, label = "headX"
    )

    val headY = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 80f
            "QIYAM" -> 80f
            "RUKU" -> 170f
            "SUJOOD" -> 280f
            "JALSA", "TASHAHHUD", "SALAM" -> 180f
            else -> 80f
        }, animationSpec = transitionSpec, label = "headY"
    )

    val shoulderX = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 140f
            "QIYAM" -> 140f
            "RUKU" -> 110f
            "SUJOOD" -> 100f
            "JALSA", "TASHAHHUD", "SALAM" -> 140f
            else -> 140f
        }, animationSpec = transitionSpec, label = "shoulderX"
    )

    val shoulderY = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 120f
            "QIYAM" -> 120f
            "RUKU" -> 170f
            "SUJOOD" -> 250f
            "JALSA", "TASHAHHUD", "SALAM" -> 210f
            else -> 120f
        }, animationSpec = transitionSpec, label = "shoulderY"
    )

    val elbowX = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 170f // Arms folded back or raised
            "QIYAM" -> 110f
            "RUKU" -> 100f
            "SUJOOD" -> 130f
            "JALSA", "TASHAHHUD", "SALAM" -> 100f
            else -> 170f
        }, animationSpec = transitionSpec, label = "elbowX"
    )

    val elbowY = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 80f  // Hands near ears
            "QIYAM" -> 155f // Hands near waist
            "RUKU" -> 210f // Resting on knees
            "SUJOOD" -> 280f // Touching floor
            "JALSA", "TASHAHHUD", "SALAM" -> 240f // Sitting on thighs
            else -> 80f
        }, animationSpec = transitionSpec, label = "elbowY"
    )

    val handX = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 160f
            "QIYAM" -> 135f
            "RUKU" -> 90f
            "SUJOOD" -> 110f
            "JALSA", "TASHAHHUD", "SALAM" -> 85f
            else -> 160f
        }, animationSpec = transitionSpec, label = "handX"
    )

    val handY = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 60f
            "QIYAM" -> 155f
            "RUKU" -> 230f
            "SUJOOD" -> 280f
            "JALSA", "TASHAHHUD", "SALAM" -> 235f
            else -> 60f
        }, animationSpec = transitionSpec, label = "handY"
    )

    val hipX = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 140f
            "QIYAM" -> 140f
            "RUKU" -> 190f
            "SUJOOD" -> 180f
            "JALSA", "TASHAHHUD", "SALAM" -> 180f
            else -> 140f
        }, animationSpec = transitionSpec, label = "hipX"
    )

    val hipY = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 220f
            "QIYAM" -> 220f
            "RUKU" -> 170f
            "SUJOOD" -> 210f
            "JALSA", "TASHAHHUD", "SALAM" -> 270f
            else -> 220f
        }, animationSpec = transitionSpec, label = "hipY"
    )

    val kneeX = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 140f
            "QIYAM" -> 140f
            "RUKU" -> 190f
            "SUJOOD" -> 220f
            "JALSA", "TASHAHHUD", "SALAM" -> 130f
            else -> 140f
        }, animationSpec = transitionSpec, label = "kneeX"
    )

    val kneeY = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 270f
            "QIYAM" -> 270f
            "RUKU" -> 230f
            "SUJOOD" -> 280f
            "JALSA", "TASHAHHUD", "SALAM" -> 280f
            else -> 270f
        }, animationSpec = transitionSpec, label = "kneeY"
    )

    val footX = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 140f
            "QIYAM" -> 140f
            "RUKU" -> 190f
            "SUJOOD" -> 190f
            "JALSA", "TASHAHHUD", "SALAM" -> 190f
            else -> 140f
        }, animationSpec = transitionSpec, label = "footX"
    )

    val footY = animateFloatAsState(
        targetValue = when (pose) {
            "TAKBEER" -> 320f
            "QIYAM" -> 320f
            "RUKU" -> 320f
            "SUJOOD" -> 320f
            "JALSA", "TASHAHHUD", "SALAM" -> 320f
            else -> 320f
        }, animationSpec = transitionSpec, label = "footY"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Sizing multipliers to fit perfectly in available canvas space
        val scaleX = width / 300f
        val scaleY = height / 360f

        val head = Offset(headX.value * scaleX, headY.value * scaleY)
        val neck = Offset(shoulderX.value * scaleX, (shoulderY.value - 20) * scaleY)
        val shoulder = Offset(shoulderX.value * scaleX, shoulderY.value * scaleY)
        val elbow = Offset(elbowX.value * scaleX, elbowY.value * scaleY)
        val hand = Offset(handX.value * scaleX, handY.value * scaleY)
        val hip = Offset(hipX.value * scaleX, hipY.value * scaleY)
        val knee = Offset(kneeX.value * scaleX, kneeY.value * scaleY)
        val foot = Offset(footX.value * scaleX, footY.value * scaleY)

        val prayerMatY = 325f * scaleY

        // 1. Draw Green Prayer Mat with gradient
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFF1B4D3E), Color(0xFF2E8B57))
            ),
            topLeft = Offset(10f * scaleX, prayerMatY),
            size = androidx.compose.ui.geometry.Size(280f * scaleX, 15f * scaleY),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * scaleX, 6f * scaleY)
        )

        // Draw elegant yellow fringes on the prayer mat
        drawRect(
            color = Color(0xFFD4AF37),
            topLeft = Offset(10f * scaleX, prayerMatY),
            size = androidx.compose.ui.geometry.Size(8f * scaleX, 15f * scaleY)
        )
        drawRect(
            color = Color(0xFFD4AF37),
            topLeft = Offset(272f * scaleX, prayerMatY),
            size = androidx.compose.ui.geometry.Size(8f * scaleX, 15f * scaleY)
        )

        // 2. Draw Stick Figure skeletal connections with rounded joints
        val boneColor = Color(0xFF1B4D3E)
        val boneStroke = 8f * scaleX

        // Spine (Shoulder to Hip)
        drawLine(
            color = boneColor,
            start = shoulder,
            end = hip,
            strokeWidth = boneStroke,
            cap = StrokeCap.Round
        )

        // Neck
        drawLine(
            color = boneColor,
            start = neck,
            end = shoulder,
            strokeWidth = boneStroke,
            cap = StrokeCap.Round
        )

        // Arm (Shoulder -> Elbow -> Hand)
        drawLine(
            color = boneColor,
            start = shoulder,
            end = elbow,
            strokeWidth = boneStroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = boneColor,
            start = elbow,
            end = hand,
            strokeWidth = boneStroke,
            cap = StrokeCap.Round
        )

        // Legs (Hip -> Knee -> Foot)
        drawLine(
            color = boneColor,
            start = hip,
            end = knee,
            strokeWidth = boneStroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = boneColor,
            start = knee,
            end = foot,
            strokeWidth = boneStroke,
            cap = StrokeCap.Round
        )

        // 3. Draw head
        drawCircle(
            color = boneColor,
            radius = 18f * scaleX,
            center = head
        )

        // Draw a tiny prayer cap (Kufi/Topi) on the head
        val capBrush = Brush.linearGradient(
            colors = listOf(Color.White, Color(0xFFE5C17B))
        )
        drawArc(
            brush = capBrush,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(head.x - 18f * scaleX, head.y - 25f * scaleY),
            size = androidx.compose.ui.geometry.Size(36f * scaleX, 18f * scaleY)
        )

        // Tashahhud indicator: Draw a tiny pointing finger indicator
        if (pose == "TASHAHHUD") {
            drawCircle(
                color = Color(0xFFD4AF37),
                radius = 4f * scaleX,
                center = Offset(hand.x - 8f * scaleX, hand.y - 4f * scaleY)
            )
        }
    }
}

// --- JANAZAH PRAYER TUTORIAL SCREEN ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JanazahScreen(viewModel: IslamicViewModel, onBack: () -> Unit) {
    var activeTakbeer by remember { mutableStateOf(1) }
    val isSpeaking by viewModel.isTtsSpeaking.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val takbeerExplanation = remember(activeTakbeer) {
        getExplanationForJanazahTakbeer(activeTakbeer)
    }

    // Lifted state for adjustable clearly visible letters
    var arabicFontSize by remember { mutableStateOf(30f) }

    LaunchedEffect(activeTakbeer) {
        viewModel.stopSpeaking()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Salat al-Janazah Guide",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Adjustable clearly visible letters slider
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = "Text Size Control",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Arabic Letter Size",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "A-",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable { if (arabicFontSize > 22f) arabicFontSize -= 2f }
                            .padding(4.dp)
                    )
                    Slider(
                        value = arabicFontSize,
                        onValueChange = { arabicFontSize = it },
                        valueRange = 22f..48f,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = "A+",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clickable { if (arabicFontSize < 48f) arabicFontSize += 2f }
                            .padding(4.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Visual Banner
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.img_janazah_banner_1783013018372),
                            contentDescription = "Janazah Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.45f))
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Salat al-Janazah Tutorial",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Learn the four Takbeers, Duas, and unique funeral rules.",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Info Card (Unique Janazah Rules)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Note: Salat al-Janazah is performed entirely standing. There is no Ruku (bowing) or Sujood (prostration). All supplicants face the Qibla in rows behind the Imam.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Takbeer Progress Indicators
            item {
                Text(
                    text = "Select Takbeer Phase",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 1..4) {
                        Card(
                            onClick = { activeTakbeer = i },
                            colors = CardDefaults.cardColors(
                                containerColor = if (activeTakbeer == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$i",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (activeTakbeer == i) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Interactive Standing Illustration
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // For Janazah we always stand straight, hands folded (QIYAM), but we do Takbeer raise sometimes.
                        val poseToDraw = if (activeTakbeer == 1) "TAKBEER" else "QIYAM"
                        SalahPostureCanvas(
                            pose = poseToDraw,
                            modifier = Modifier
                                .size(140.dp)
                                .padding(8.dp)
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = if (activeTakbeer == 1) "Opening Takbeer" else "Folded Standing",
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Step Explanation Content
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Takbeer $activeTakbeer of 4",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = takbeerExplanation.title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (takbeerExplanation.arabicPhrase.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = takbeerExplanation.arabicPhrase,
                                fontFamily = LateefFontFamily,
                                fontSize = arabicFontSize.sp,
                                color = Color(0xFF1B4D3E),
                                fontWeight = FontWeight.Bold,
                                lineHeight = (arabicFontSize * 1.5).sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (takbeerExplanation.translation.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = takbeerExplanation.translation,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                lineHeight = 17.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = takbeerExplanation.description,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Audio Recitation & Guidance Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (takbeerExplanation.arabicPhrase.isNotEmpty()) {
                                Button(
                                    onClick = {
                                        if (isSpeaking) {
                                            viewModel.stopSpeaking()
                                        } else {
                                            viewModel.speakText(takbeerExplanation.arabicPhrase, "ar")
                                            Toast.makeText(context, "Playing Arabic recitation...", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(42.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Listen Arabic",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Listen Arabic", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = {
                                    if (isSpeaking) {
                                        viewModel.stopSpeaking()
                                    } else {
                                        viewModel.speakText(takbeerExplanation.audioText, "en")
                                        Toast.makeText(context, "Playing audio guide...", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = "Listen Guide",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Listen Guide", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Previous and Next buttons for Takbeers
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (activeTakbeer > 1) activeTakbeer-- },
                        enabled = activeTakbeer > 1,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Prev", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            if (activeTakbeer < 4) {
                                activeTakbeer++
                            } else {
                                Toast.makeText(context, "MashaAllah! Janazah Prayer tutorial finished.", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(if (activeTakbeer == 4) "Finish" else "Next Takbeer")
                        Spacer(modifier = Modifier.width(4.dp))
                        if (activeTakbeer < 4) {
                            Icon(Icons.Default.ArrowForwardIos, contentDescription = "Next", modifier = Modifier.size(16.dp))
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "Done", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- GOOGLE PLAY STORE COMPLIANCE & PRIVACY SCREEN ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolicyComplianceScreen(onBack: () -> Unit) {
    var expandedSection by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Policy, Privacy & Compliance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Introductory Header
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Shield Compliance",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Google Play Safety Certified",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "This app is fully transparent and compliant with Google Play Store Policies. We strictly operate offline, respect individual privacy, request minimal local permissions, and transmit zero personal credentials or data metrics.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Expandable Privacy Policy Row
            item {
                ComplianceRow(
                    title = "Privacy Policy",
                    icon = Icons.Default.Lock,
                    isExpanded = expandedSection == "Privacy",
                    onClick = { expandedSection = if (expandedSection == "Privacy") null else "Privacy" },
                    content = "1. Data Collection: We do not collect, store, or transmit any personally identifiable information (PII). Everything is processed locally on your hardware.\n\n" +
                            "2. Device Location: Approximate location permission is requested solely to calculate prayer times and compute Qibla compass alignment locally on your device. This data is never sent to any external server.\n\n" +
                            "3. Audio Recording: The AI assistant voice activation and TTS components process speech local to the application sandboxes or utilize standard official system engines.\n\n" +
                            "4. Changes: We reserve the right to update this policy as needed. Changes are published immediately inside the application interface."
                )
            }

            // Expandable Terms of Use Row
            item {
                ComplianceRow(
                    title = "Terms of Use (EULA)",
                    icon = Icons.Default.Gavel,
                    isExpanded = expandedSection == "Terms",
                    onClick = { expandedSection = if (expandedSection == "Terms") null else "Terms" },
                    content = "1. Purpose: This application provides educational information regarding prayer times, Quranic script reading, Islamic guidance, and supplementary learning steps.\n\n" +
                            "2. Limitation of Liability: Educational guides and tools are generated in light of standard Islamic practices. The content is for informational and individual spiritual learning purposes.\n\n" +
                            "3. Free Utility: This app is a non-commercial, completely free utility. We do not sell visual features, locked screens, or subscriptions.\n\n" +
                            "4. Acceptable Conduct: Users must not reverse-engineer, alter resources, or distribute unofficial APK structures under misleading brand names."
                )
            }

            // Expandable Data Safety Row
            item {
                ComplianceRow(
                    title = "Data Safety Declaration",
                    icon = Icons.Default.VerifiedUser,
                    isExpanded = expandedSection == "Safety",
                    onClick = { expandedSection = if (expandedSection == "Safety") null else "Safety" },
                    content = "1. Data Shared: No data is shared with third-party companies or promotional networks.\n\n" +
                            "2. Data Collected: No user profiles, behavioral telemetry, location logs, or speech recording audio is gathered.\n\n" +
                            "3. Security Measures: The application relies on Android Standard sandboxing, local SQLite storage with Room, and device-bound configurations to prevent data intercepts.\n\n" +
                            "4. Right of Deletion: Because all configurations are on-device only, clearing the app storage or uninstalling the app permanently purges all local bookmarks, custom lists, and logs."
                )
            }

            // App Metadata Status Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Developer Support & Contact",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Email: yoursupport@email.com\n" +
                                    "Version: 2.1.0-Release\n" +
                                    "Signing: Official Google Play Release Configured\n" +
                                    "Target SDK: Android 14 (API 34) Compliant",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComplianceRow(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onClick: () -> Unit,
    content: String
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = content,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// --- DATA ACCESS HELPER METHODS ---

private fun getStepsForPrayer(prayerName: String): List<SalahStep> {
    return when (prayerName) {
        "Fajr" -> listOf(
            SalahStep(
                title = "Niyyah (Intention) & Opening Takbeer",
                arabicPhrase = "اللَّهُ أَكْبَرُ",
                translation = "Allah is the Greatest",
                description = "Stand facing the Qibla, formulate the intention in your heart to perform Fajr prayer, raise your hands to your ears/shoulders, and recite 'Allahu Akbar' to commence.",
                audioText = "Step 1. Form the intention of Fajr prayer, stand straight facing Qibla, raise your hands to your ears and recite Allahu Akbar.",
                pose = "TAKBEER"
            ),
            SalahStep(
                title = "Standing Position (Qiyam)",
                arabicPhrase = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                description = "Fold your hands over your chest (right over left). Quietly recite Surah Al-Fatihah, followed by any other short Surah of the Quran, keeping your gaze on the place of prostration.",
                audioText = "Step 2. Fold your hands on your chest, keep your gaze down, and recite Surah Al-Fatihah followed by another passage of the Quran.",
                pose = "QIYAM"
            ),
            SalahStep(
                title = "Bowing Position (Ruku)",
                arabicPhrase = "سُبْحَانَ رَبِّيَ الْعَظِيمِ",
                translation = "Glory be to my Lord, the Almighty",
                description = "Bow down at a 90-degree angle, placing your hands firmly on your knees. Recite 'Subhana Rabbiyal Azeem' three times.",
                audioText = "Step 3. Bow down with a straight back, placing hands on knees, and say Subhana Rabbiyal Azeem three times.",
                pose = "RUKU"
            ),
            SalahStep(
                title = "Rising from Bowing (Qiyam)",
                arabicPhrase = "سَمِعَ اللَّهُ لِمَنْ حَمِدَهُ",
                translation = "Allah hears those who praise Him",
                description = "Return to standing completely straight with arms at your sides. Say 'Sami' Allahu liman hamidah', followed by 'Rabbana wa lakal hamd'.",
                audioText = "Step 4. Stand up straight again, and say Sami Allahu liman hamidah, Rabbana wa lakal hamd.",
                pose = "TAKBEER"
            ),
            SalahStep(
                title = "First Prostration (Sujood)",
                arabicPhrase = "سُبْحَانَ رَبِّيَ الْأَعْلَى",
                translation = "Glory be to my Lord, the Most High",
                description = "Prostrate on the floor, ensuring your forehead, nose, palms, knees, and toes touch the ground. Recite 'Subhana Rabbiyal A'la' three times.",
                audioText = "Step 5. Go down into prostration on the floor, and say Subhana Rabbiyal A'la three times.",
                pose = "SUJOOD"
            ),
            SalahStep(
                title = "Sitting Position (Jalsa)",
                arabicPhrase = "رَبِّ اغْفِرْ لِي",
                translation = "O my Lord, forgive me",
                description = "Rise from Sujood and sit upright on your heels. Pause briefly and recite 'Rabbighfir lee' before the second Sujood.",
                audioText = "Step 6. Rise from prostration to sit upright on your heels, saying Rabbighfir lee.",
                pose = "JALSA"
            ),
            SalahStep(
                title = "Second Prostration (Sujood)",
                arabicPhrase = "سُبْحَانَ رَبِّيَ الْأَعْلَى",
                translation = "Glory be to my Lord, the Most High",
                description = "Prostrate on the floor again for the second time, saying 'Subhana Rabbiyal A'la' three times.",
                audioText = "Step 7. Prostrate once more on the floor, saying Subhana Rabbiyal A'la three times.",
                pose = "SUJOOD"
            ),
             SalahStep(
                title = "Sitting for Tashahhud & Durood",
                description = "In the final sitting posture after completing the second Rak'ah, sit on your heels. Recite Attahiyyaat, raising your right index finger during Shahadah, followed by Durood Ibrahim and custom prayers.",
                audioText = "Step 8. Sit on your heels for the final Tashahhud, reciting Attahiyyaat, Durood Ibrahim, and raising your index finger during the testimonial.",
                pose = "TASHAHHUD"
            ),
            SalahStep(
                title = "Concluding Salam (Taslim)",
                arabicPhrase = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ",
                translation = "Peace and blessings of Allah be upon you",
                description = "Turn your head to the right and say 'As-salamu alaykum wa rahmatullah', then turn your head to the left and repeat the same to conclude your prayer.",
                audioText = "Step 9. Conclude the prayer by turning your head right saying As-salamu alaykum wa rahmatullah, then repeat looking to the left.",
                pose = "SALAM"
            )
        )
        else -> listOf( // General template for 3 or 4 Rak'ah prayers (Dhuhr, Asr, Maghrib, Isha)
            SalahStep(
                title = "Niyyah & Opening Takbeer",
                arabicPhrase = "اللَّهُ أَكْبَرُ",
                translation = "Allah is the Greatest",
                description = "Formulate the intention in your heart for the selected prayer, raise your hands to your ears, and say 'Allahu Akbar' to begin.",
                audioText = "Step 1. Form your intention, raise your hands to your ears and recite Allahu Akbar.",
                pose = "TAKBEER"
            ),
            SalahStep(
                title = "Standing Position (Qiyam)",
                arabicPhrase = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                description = "Fold your hands over your chest. Recite Surah Al-Fatihah and another Surah.",
                audioText = "Step 2. Stand straight, hands folded on your chest, and recite Al-Fatihah and another Surah.",
                pose = "QIYAM"
            ),
            SalahStep(
                title = "Bowing Position (Ruku)",
                arabicPhrase = "سُبْحَانَ رَبِّيَ الْعَظِيمِ",
                translation = "Glory be to my Lord, the Almighty",
                description = "Bow down at a 90-degree angle, placing your hands on your knees, and say 'Subhana Rabbiyal Azeem' three times.",
                audioText = "Step 3. Bow down, hands on knees, saying Subhana Rabbiyal Azeem three times.",
                pose = "RUKU"
            ),
            SalahStep(
                title = "Rising from Bowing (Qiyam)",
                arabicPhrase = "سَمِعَ اللَّهُ لِمَنْ حَمِدَهُ",
                translation = "Allah hears those who praise Him",
                description = "Stand up completely straight, hands at sides, and say 'Sami' Allahu liman hamidah', 'Rabbana wa lakal hamd'.",
                audioText = "Step 4. Stand up completely straight, reciting Sami' Allahu liman hamidah.",
                pose = "TAKBEER"
            ),
            SalahStep(
                title = "First Prostration (Sujood)",
                arabicPhrase = "سُبْحَانَ رَبِّيَ الْأَعْلَى",
                translation = "Glory be to my Lord, the Most High",
                description = "Prostrate on the ground, forehead and palms touching the floor, and say 'Subhana Rabbiyal A'la' three times.",
                audioText = "Step 5. Prostrate on the floor, saying Subhana Rabbiyal A'la three times.",
                pose = "SUJOOD"
            ),
            SalahStep(
                title = "Sitting Position (Jalsa)",
                arabicPhrase = "رَبِّ اغْفِرْ لِي",
                translation = "O my Lord, forgive me",
                description = "Rise from prostration and sit on your heels briefly, reciting 'Rabbighfir lee'.",
                audioText = "Step 6. Sit upright on your heels briefly, reciting Rabbighfir lee.",
                pose = "JALSA"
            ),
            SalahStep(
                title = "Second Prostration (Sujood)",
                arabicPhrase = "سُبْحَانَ رَبِّيَ الْأَعْلَى",
                translation = "Glory be to my Lord, the Most High",
                description = "Return to prostration and say 'Subhana Rabbiyal A'la' three times.",
                audioText = "Step 7. Prostrate for the second time, saying Subhana Rabbiyal A'la.",
                pose = "SUJOOD"
            ),
            SalahStep(
                title = "Middle Tashahhud (After 2 Rak'ahs)",
                description = "Sit upright on your heels. Recite Attahiyyaat, raising your index finger during Shahadah, before rising to perform the remaining Rak'ahs.",
                audioText = "Step 8. After two Rak'ahs, sit upright on your heels to recite Attahiyyaat and raise your index finger.",
                pose = "TASHAHHUD"
            ),
            SalahStep(
                title = "Final Tashahhud & Supplications",
                description = "At the end of the final Rak'ah (3rd for Maghrib, 4th for Dhuhr/Asr/Isha), sit upright. Recite Attahiyyaat, Durood Ibrahim, and make your private supplications.",
                audioText = "Step 9. In the final sitting posture, recite Attahiyyaat and Durood Ibrahim fully.",
                pose = "TASHAHHUD"
            ),
            SalahStep(
                title = "Concluding Salam (Taslim)",
                arabicPhrase = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ",
                translation = "Peace and blessings of Allah be upon you",
                description = "Turn your head right and say 'As-salamu alaykum wa rahmatullah', then turn left and say the same.",
                audioText = "Step 10. Turn your head to the right and say Salam, then repeat looking to the left.",
                pose = "SALAM"
            )
        )
    }
}

private fun getExplanationForJanazahTakbeer(takbeer: Int): SalahStep {
    return when (takbeer) {
        1 -> SalahStep(
            title = "The 1st Takbeer & Recitation of Surah Al-Fatihah",
            arabicPhrase = "اللَّهُ أَكْبَرُ",
            description = "Raise your hands to your ears and declare 'Allahu Akbar' to begin Salat al-Janazah. Fold your hands on your chest as in daily prayers, and recite Surah Al-Fatihah silently.",
            audioText = "First Takbeer. Raise hands to ears saying Allahu Akbar, fold hands on chest, and recite Surah Al-Fatihah silently.",
            pose = "TAKBEER"
        )
        2 -> SalahStep(
            title = "The 2nd Takbeer & Durood Ibrahim",
            arabicPhrase = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ...",
            description = "The Imam declares 'Allahu Akbar'. Without raising your hands (or raising them, depending on school), silently recite Durood Ibrahim (the blessings upon the Prophet, peace be upon him).",
            audioText = "Second Takbeer. Declare Allahu Akbar, and recite Durood Ibrahim, sending peace and blessings upon Prophet Muhammad.",
            pose = "QIYAM"
        )
        3 -> SalahStep(
            title = "The 3rd Takbeer & Supplication for the Deceased",
            arabicPhrase = "اللَّهُمَّ اغْفِرْ لِحَيِّنَا وَمَيِّتِنَا وَشَاهِدِنَا وَغَائِبِنَا...",
            translation = "O Allah, forgive our living and our dead, those who are present and those who are absent...",
            description = "The Imam declares 'Allahu Akbar' for the third time. Recite the beautiful Janazah Dua asking for the forgiveness and peace of the deceased companion, relative, or child.",
            audioText = "Third Takbeer. Declare Allahu Akbar, and recite the custom funeral prayer for the deceased, seeking Allah's mercy and forgiveness.",
            pose = "QIYAM"
        )
        else -> SalahStep(
            title = "The 4th Takbeer & Concluding Salam",
            arabicPhrase = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ",
            translation = "Peace and blessings of Allah be upon you",
            description = "The Imam declares the final 'Allahu Akbar'. Pause briefly, make general dua for the Muslim community, then turn your head to the right and left saying 'As-salamu alaykum wa rahmatullah' to conclude.",
            audioText = "Fourth Takbeer. Declare Allahu Akbar, pause briefly, and turn your head right and left saying Salam to conclude the funeral prayer.",
            pose = "SALAM"
        )
    }
}

// --- AZAN (CALL TO PRAYER) LEARNING SCREEN ---

data class AzanStep(
    val title: String,
    val arabicPhrase: String,
    val transliteration: String,
    val translationEnglish: String,
    val translationUrdu: String,
    val explanation: String,
    val audioTextEnglish: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzanGuideScreen(viewModel: IslamicViewModel, onBack: () -> Unit) {
    val isSpeaking by viewModel.isTtsSpeaking.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val azanSteps = remember {
        listOf(
            AzanStep(
                title = "The Takbeer (x4)",
                arabicPhrase = "اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ",
                transliteration = "Allāhu Akbar, Allāhu Akbar",
                translationEnglish = "Allah is the Greatest, Allah is the Greatest",
                translationUrdu = "اللہ سب سے بڑا ہے، اللہ سب سے بڑا ہے",
                explanation = "The call begins by declaring the supreme greatness of Allah four times. It calls hearts to detach from worldly worries and focus on the Divine.",
                audioTextEnglish = "Allahu Akbar, Allahu Akbar. Allah is the Greatest, Allah is the Greatest."
            ),
            AzanStep(
                title = "The Shahadah of Tawheed (x2)",
                arabicPhrase = "أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا اللَّهُ",
                transliteration = "Ashhadu an lā ilāha illallāh",
                translationEnglish = "I bear witness that there is no god but Allah",
                translationUrdu = "میں گواہی دیتا ہوں کہ اللہ کے سوا کوئی معبود نہیں",
                explanation = "Declaring the testimony of faith (Tawheed) twice, affirming that none deserves worship except Allah.",
                audioTextEnglish = "Ashhadu an la ilaha illallah. I bear witness that there is no god but Allah."
            ),
            AzanStep(
                title = "The Shahadah of Risalah (x2)",
                arabicPhrase = "أَشْهَدُ أَنَّ مُحَمَّدًا رَسُولُ اللَّهِ",
                transliteration = "Ashhadu anna Muḥammadan rasūlullāh",
                translationEnglish = "I bear witness that Muhammad is the Messenger of Allah",
                translationUrdu = "میں گواہی دیتا ہوں کہ محمدﷺ اللہ کے رسول ہیں",
                explanation = "Declaring that Prophet Muhammad (peace be upon him) is the final guide and messenger of Allah.",
                audioTextEnglish = "Ashhadu anna Muhammadan rasoolullah. I bear witness that Muhammad is the Messenger of Allah."
            ),
            AzanStep(
                title = "Call to Prayer (x2)",
                arabicPhrase = "حَيَّ عَلَى الصَّلَاةِ",
                transliteration = "Ḥayya ‘alas-ṣalāh",
                translationEnglish = "Hurry to the prayer",
                translationUrdu = "نماز کی طرف آؤ",
                explanation = "The caller invites everyone to fulfill their sacred connection with Allah through the salah.",
                audioTextEnglish = "Hayya alas-salah. Hurry to the prayer."
            ),
            AzanStep(
                title = "Call to Success (x2)",
                arabicPhrase = "حَيَّ عَلَى الْفَلَاحِ",
                transliteration = "Ḥayya ‘alal-falāḥ",
                translationEnglish = "Hurry to success",
                translationUrdu = "کامیابی کی طرف آؤ",
                explanation = "True success in this life and the hereafter lies in turning towards Allah in worship.",
                audioTextEnglish = "Hayya alal-falah. Hurry to success."
            ),
            AzanStep(
                title = "Fajr Special Invitation (x2)",
                arabicPhrase = "الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ",
                transliteration = "Aṣ-ṣalātu khayrun minan-nawm",
                translationEnglish = "Prayer is better than sleep (Only during Fajr call)",
                translationUrdu = "نماز نیند سے بہتر ہے (صرف نمازِ فجر کے لیے)",
                explanation = "An extra encouragement recited only in the dawn call, reminding us that spiritual wakefulness is superior to physical rest.",
                audioTextEnglish = "As-salatu khayrun minan-nawm. Prayer is better than sleep."
            ),
            AzanStep(
                title = "Concluding Takbeer (x2)",
                arabicPhrase = "اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ",
                transliteration = "Allāhu Akbar, Allāhu Akbar",
                translationEnglish = "Allah is the Greatest, Allah is the Greatest",
                translationUrdu = "اللہ سب سے بڑا ہے، اللہ سب سے بڑا ہے",
                explanation = "Reiterating Allah's ultimate majesty before ending the call.",
                audioTextEnglish = "Allahu Akbar, Allahu Akbar. Allah is the Greatest, Allah is the Greatest."
            ),
            AzanStep(
                title = "Concluding Tawheed (x1)",
                arabicPhrase = "لَا إِلَٰهَ إِلَّا اللَّهُ",
                transliteration = "Lā ilāha illallāh",
                translationEnglish = "There is no god but Allah",
                translationUrdu = "اللہ کے سوا کوئی معبود نہیں",
                explanation = "Ending on the ultimate word of truth and unity.",
                audioTextEnglish = "La ilaha illallah. There is no god but Allah."
            )
        )
    }

    // State for selected step
    var activeStepIndex by remember { mutableStateOf(0) }
    val currentStep = azanSteps[activeStepIndex]

    // State for adjustable font sizes
    var arabicFontSize by remember { mutableStateOf(32f) }

    LaunchedEffect(activeStepIndex) {
        viewModel.stopSpeaking()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Learn the Azan (Call to Prayer)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Adjustable clearly visible letters slider
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = "Text Size Control",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Arabic Letter Size",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "A-",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable { if (arabicFontSize > 22f) arabicFontSize -= 2f }
                            .padding(4.dp)
                    )
                    Slider(
                        value = arabicFontSize,
                        onValueChange = { arabicFontSize = it },
                        valueRange = 22f..48f,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = "A+",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clickable { if (arabicFontSize < 48f) arabicFontSize += 2f }
                            .padding(4.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Visual Banner
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.img_janazah_banner_1783013018372), // reuse banner image for aesthetic consistency
                            contentDescription = "Azan Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Learn Azan & Iqamah",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Learn the words, pronunciations, meanings, and rewards.",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Step Selector
            item {
                Text(
                    text = "Select Azan Phrase",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    azanSteps.forEachIndexed { i, step ->
                        val isSelected = activeStepIndex == i
                        Card(
                            onClick = { activeStepIndex = i },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .width(65.dp)
                                .height(46.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${i + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Main Display Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Phrase ${activeStepIndex + 1} of ${azanSteps.size}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentStep.title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large clearly visible Arabic Letters
                        Text(
                            text = currentStep.arabicPhrase,
                            fontFamily = LateefFontFamily,
                            fontSize = arabicFontSize.sp,
                            color = Color(0xFF1B4D3E),
                            fontWeight = FontWeight.Bold,
                            lineHeight = (arabicFontSize * 1.5).sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Transliteration
                        Text(
                            text = "Transliteration:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = currentStep.transliteration,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Urdu translation
                        Text(
                            text = "اردو ترجمہ:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF1B4D3E)
                        )
                        Text(
                            text = currentStep.translationUrdu,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )

                        // English translation
                        Text(
                            text = "English Translation:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = currentStep.translationEnglish,
                            fontSize = 13.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Divider
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Explanation
                        Text(
                            text = "Explanation & Spiritual Significance:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = currentStep.explanation,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Audio Voice controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (isSpeaking) {
                                        viewModel.stopSpeaking()
                                    } else {
                                        viewModel.speakText(currentStep.arabicPhrase, "ar")
                                        Toast.makeText(context, "Playing Arabic recitation...", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Recite Arabic",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Listen Arabic", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    if (isSpeaking) {
                                        viewModel.stopSpeaking()
                                    } else {
                                        viewModel.speakText(currentStep.audioTextEnglish, "en")
                                        Toast.makeText(context, "Playing English explanation...", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = "Listen Explanation",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Listen Guide", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Navigation buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (activeStepIndex > 0) activeStepIndex-- },
                        enabled = activeStepIndex > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Prev", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Previous", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { if (activeStepIndex < azanSteps.size - 1) activeStepIndex++ },
                        enabled = activeStepIndex < azanSteps.size - 1,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Next Step", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForwardIos, contentDescription = "Next", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
