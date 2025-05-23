package com.example.gameswiper.composable

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.gameswiper.R
import com.example.gameswiper.network.GamesWrapper
import java.io.File
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gameswiper.model.GamesViewModel
import com.example.gameswiper.repository.GameRepository


@Composable
fun MainScreen(modifier: Modifier, context: Context, wrapper: GamesWrapper, viewModel: GamesViewModel, gamesRepository: GameRepository){
    val offsetX = remember { Animatable(0f) }


    val coroutineScope = rememberCoroutineScope()
    val maxRotationAngle = 15f
    var imageUrl by remember { mutableStateOf(" ") }
    val themesFile = File(context.filesDir, "themes.json")
    var borderColor by remember { mutableStateOf(Color(0xFF4100B1)) }

    wrapper.wrapThemes(context)
    val images by viewModel.images.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val games by viewModel.games.collectAsState()
    var currentImage = images.getOrNull(currentIndex)

    //LaunchedEffect (Unit){viewModel.fetchGames(context, listOf(16, 5), listOf(6), wrapper)}
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally){

        Row(
            modifier = Modifier.fillMaxSize().weight(90f)){
            Column(
                modifier =  Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Card(
                    colors = CardColors(
                        Color(0xFF4635B1), Color(0xFFBBBBBB),
                        Color(0xff9933ff), Color(0xFF816BA8)
                    ),
                    modifier = Modifier
                        .height(600.dp)
                        .width(350.dp)
                        .offset{IntOffset(offsetX.value.roundToInt(), 0)}
                        .graphicsLayer {
                            rotationZ = (offsetX.value / 1000f) * maxRotationAngle
                        }
                        .pointerInput(Unit){
                            detectDragGestures (
                                onDrag = {change, dragAmount ->
                                    change.consume()
                                    coroutineScope.launch{
                                        offsetX.snapTo(offsetX.value + dragAmount.x)
                                    }
                                },
                                onDragEnd = {
                                    coroutineScope.launch{
                                        if(offsetX.value < -100f){
                                            offsetX.animateTo(
                                                targetValue = -1500f,
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                            delay(200)
                                            offsetX.snapTo(0f)
                                            print(viewModel.images.value.size)
                                            viewModel.nextImage()

                                        }
                                        else if(offsetX.value > 100f){
                                            offsetX.animateTo(
                                                targetValue = 1500f,
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                            delay(200)
                                            offsetX.snapTo(0f)
                                            println(games[currentIndex])
                                            gamesRepository.addGame(games[currentIndex])
                                            viewModel.nextImage()

                                        }
                                        else {
                                            offsetX.animateTo(
                                                targetValue = 0f,
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                        }
                                    }
                                }
                            )
                        }

                ) {

                    Box(
                        modifier = Modifier
                            .size(350.dp)

                    )

                    {
                        if(currentImage != null){
                            println(currentImage)

                            AsyncImage(
                                model = currentImage,
                                contentDescription = "Example Image",
                                modifier = Modifier
                                    .height(350.dp)
                                    .padding(start = 48.dp, top = 15.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(10.dp, borderColor, RoundedCornerShape(8.dp))
                            )



                        }
                        else{
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color(0xFFFFF2AF)
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    if(currentImage != null) {
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().padding(5.dp)) {
                            Box()
                            {
                            Text(text = games[currentIndex].name, fontSize = 20.sp, textAlign = TextAlign.Center,
                                modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(borderColor).padding(5.dp))
                            }
                        }
                    }

                    if(offsetX.value>100){
                        borderColor = Color(0xFF77B254)
                        Icon(
                            painter = painterResource(R.drawable.plus),
                            contentDescription = "Add",
                            modifier = Modifier.size(64.dp).align(Alignment.CenterHorizontally)
                        )
                    }
                    else if(offsetX.value < -100){
                        borderColor = Color(0xFFBE3144)
                        Icon(
                            painter = painterResource(R.drawable.delete),
                            contentDescription = "Delete",
                            modifier = Modifier.size(64.dp).align(Alignment.CenterHorizontally)
                        )
                    }
                    else borderColor = Color(0xFF4100B1)

                }
            }
        }
    }
    
}

@Composable
fun ImageBackground(modifier: Modifier, context: Context, logOut: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.background2),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize()
        )

        var current by remember { mutableIntStateOf(1) }
        val wrapper = GamesWrapper()
        val viewModel: GamesViewModel = viewModel()
        wrapper.getStaticToken()
        val gamesRepository = GameRepository()
        var currentScreen by remember { mutableStateOf("home_screen") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { current = -1; currentScreen = "settings_screen"},
                    modifier = Modifier.size(50.dp).clip(CircleShape),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4100B1))
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", modifier = Modifier.size(30.dp), tint = Color.White)
                }
                Button(onClick = { current = 1; currentScreen = "library_screen"},
                    modifier = Modifier.size(50.dp).clip(CircleShape),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4100B1))
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Library", modifier = Modifier.size(30.dp), tint = Color.White)
                }
            }
        }
        AnimatedVisibility(
            visible = currentScreen == "home_screen",
            enter = slideInHorizontally(
                initialOffsetX = { -it * current },
                animationSpec = tween(500)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it * current },
                animationSpec = tween(500)
            )
        ) {
            MainScreen(modifier, context, wrapper, viewModel, gamesRepository)
        }

        AnimatedVisibility(
            visible = currentScreen == "settings_screen",
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(500)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(500)
            )
        ) {
            SettingsScreen(onBackPressed = { currentScreen = "home_screen"}, context, logOut, wrapper)
        }


        AnimatedVisibility(
            visible = currentScreen == "library_screen",
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(500)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(500)
            )
        ) {
            LibraryScreen(
                context = context,
                viewModel = viewModel,
                gameRepository = gamesRepository,
                gamesWrapper = wrapper,
                onBackPressed = { currentScreen = "home_screen" }
            )
        }

    }
}