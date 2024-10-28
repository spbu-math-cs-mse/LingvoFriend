package com.example.lingvofriend.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lingvofriend.llmApi.Message
import com.example.lingvofriend.llmApi.buildClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("SignIn")
            else -> Unit
        }
    }

    val chatMessages =
        remember {
            mutableStateListOf<Message>()
        }

    var userMessage by remember { mutableStateOf("") }
    var aiMessage by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .weight(1f),
            state = listState,
        ) {
            items(chatMessages) { chatMessage ->

                val arrangement =
                    if (chatMessage.role == "user") Arrangement.End else Arrangement.Start
                val color = if (chatMessage.role == "user") Color.LightGray else Color(0xFFD0F0C0)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = arrangement,
                ) {
                    ChatBubble(
                        chatMessage.text,
                        color,
                    )
                }
            }
        }

        OutlinedTextField(
            value = userMessage,
            onValueChange = { userMessage = it },
            placeholder = { Text("Message") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(25.dp),
                    ),
            shape = RoundedCornerShape(25.dp),
            colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (userMessage.isNotEmpty()) {
                            chatMessages.add(Message("user", userMessage))
                        }

                        coroutineScope.launch {
                            val aiResponse = buildClient(chatMessages.toList())
                            chatMessages.add(Message("assistant", aiResponse))
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = "Send msg",
                    )
                }
            },
        )
    }
}

@Composable
fun ChatBubble(
    message: String,
    color: Color = Color.LightGray,
) {
    Text(
        text = message,
        modifier =
            Modifier
                .padding(8.dp)
                .background(color, shape = RoundedCornerShape(8.dp))
                .padding(12.dp),
        color = Color.Black,
        fontSize = 16.sp,
    )
}
