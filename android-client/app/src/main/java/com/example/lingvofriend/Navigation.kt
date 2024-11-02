package com.example.lingvofriend

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lingvofriend.pages.AuthViewModel
import com.example.lingvofriend.pages.HomePage
import com.example.lingvofriend.pages.SignInPage
import com.example.lingvofriend.pages.SignUpPage

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
) {
    val navController = rememberNavController()

    // navhost gets navController and uses it to navigate between pages
    // the default destination is always SignIn screen
    NavHost(navController = navController, startDestination = "SignIn", builder = {
        composable("SignIn") {
            SignInPage(modifier, navController, authViewModel)
        }
        composable("SignUp") {
            SignUpPage(modifier, navController, authViewModel)
        }
        composable("HomePage") {
            HomePage(modifier, navController, authViewModel)
        }
    })
}
