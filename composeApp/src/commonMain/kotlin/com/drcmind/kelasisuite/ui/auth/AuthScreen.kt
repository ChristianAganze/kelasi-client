package com.drcmind.kelasisuite.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.ui.components.AppColors
import com.drcmind.kelasisuite.ui.components.AuthInputField
import com.drcmind.kelasisuite.ui.components.PrimaryButton
import org.koin.compose.koinInject

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    // Handle success state
    if (state.authState is AuthState.Success) {
        onAuthSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Surface.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AuthFormSection(
                state = state,
                onEmailChange = viewModel::updateEmail,
                onPasswordChange = viewModel::updatePassword,
                onRememberMeChange = viewModel::updateRememberMe,
                onLogin = viewModel::login,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AuthFormSection(
    state: AuthViewModelState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(AppColors.Surface.container, RoundedCornerShape(24.dp))
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        // Logo Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(AppColors.primary, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "K",
                    color = AppColors.onPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Kelasi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.primary,
                letterSpacing = (-0.5).sp
            )
        }

        // Title & Subtitle
        Text(
            text = "Connexion",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.surfaceContent,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Veuillez entrer vos coordonnées pour accéder à votre compte.",
            fontSize = 14.sp,
            color = AppColors.Text.secondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Email Input
        AuthInputField(
            value = state.email,
            onValueChange = onEmailChange,
            label = "Adresse e-mail",
            placeholder = "nom@exemple.com",
            icon = Icons.Default.Mail,
            isError = state.emailError != null,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (state.emailError != null) {
            Text(
                text = state.emailError,
                fontSize = 12.sp,
                color = AppColors.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Password Input
        AuthInputField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = "Mot de passe",
            placeholder = "••••••••",
            icon = Icons.Default.Lock,
            isPassword = true,
            isError = state.passwordError != null,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (state.passwordError != null) {
            Text(
                text = state.passwordError!!,
                fontSize = 12.sp,
                color = AppColors.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Remember Me & Forgot Password
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.rememberMe,
                    onCheckedChange = onRememberMeChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = AppColors.primary,
                        uncheckedColor = AppColors.Text. secondary
                    ),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Se souvenir de moi",
                    fontSize = 14.sp,
                    color = AppColors.Text.tertiary
                )
            }

            Text(
                text = "Mot de passe oublié ?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.Text.disabled,
                modifier = Modifier
            )
        }

        // Error State Display
        if (state.authState is AuthState.Error) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.errorBackground, RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = (state.authState as AuthState.Error).message,
                    fontSize = 14.sp,
                    color = AppColors.error
                )
            }
        }

        // Login Button
        PrimaryButton(
            text = "Se connecter",
            isLoading = state.authState is AuthState.Loading,
            onClick = onLogin,
            modifier = Modifier.padding(bottom = 20.dp)
        )


    }
}