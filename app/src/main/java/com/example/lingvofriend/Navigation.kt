package com.example.lingvofriend

import com.example.lingvofriend.pages.SignInPage
import com.example.lingvofriend.pages.HomePage
import com.example.lingvofriend.pages.SignUpPage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lingvofriend.pages.AuthViewModel


@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "SignIn", builder = {
        composable("SignIn"){
            SignInPage(modifier, navController, authViewModel)
        }
        composable("SignUp"){
            SignUpPage(modifier, navController, authViewModel)
        }
        composable("HomePage") {
            HomePage(modifier, navController, authViewModel)
        }
    })
}