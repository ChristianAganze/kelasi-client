package com.drcmind.kelasisuite.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.util.AdaptiveUtil
import com.drcmind.kelasisuite.ui.components.AppColors
import com.drcmind.kelasisuite.ui.components.AuthInputField
import com.drcmind.kelasisuite.ui.components.PrimaryButton
import org.koin.compose.koinInject

@Composable
fun AuthScreen(
    onAuthSuccess: (String) -> Unit,
    viewModel: AuthViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.authState) {
        if (state.authState is AuthState.Success) {
            onAuthSuccess((state.authState as AuthState.Success).role)
        }
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val width = maxWidth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Surface.background), // Assure-toi que c'est un gris très clair/blanc
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f)
                    .background(
                        AppColors.Surface.container,
                        RoundedCornerShape(32.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuthFormSection(
                    state = state,
                    onEmailChange = viewModel::updateEmail,
                    onPasswordChange = viewModel::updatePassword,
                    onRememberMeChange = viewModel::updateRememberMe,
                    onLogin = viewModel::login,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 48.dp)
                )
                if (!AdaptiveUtil.isCompact(width) && !AdaptiveUtil.isMedium(width)) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(
                                color = androidx.compose.ui.graphics.Color(0xFF0C0C0C), // visual-card-bg
                                shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)
                            )
                            .padding(48.dp)
                    ) {
                        VisualBrandingContent()
                    }
                }

            }
        }
    }
}

@Composable
fun VisualBrandingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Label du haut
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(50)
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Kelasi Suite",
                color = AppColors.disabled,
                fontSize = 14.sp
            )
        }

        // Contenu Central
        Column {
            Text(
                text = "Bienvenue chez Kelasi",
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 44.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Kelasi aide les institution scolaire et académique à s'organiser et digitaliser la gestion de leur établissement.",
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Simulation de la "Glass Card"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        androidx.compose.ui.graphics.Color.White.copy(alpha = 0.05f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp)
            ) {
                Text(
                    "Trouvez votre rythme d'apprentissage dès maintenant",
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Footer
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                "© 2026 DrcMind",
                color = AppColors.disabled,
                fontSize = 12.sp
            )
            TextButton(
                onClick = {}

            ) {
                Text(
                    "A propos",
                    color = AppColors.disabled,
                    fontSize = 12.sp
                )
            }
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
                text = state.passwordError,
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
                        uncheckedColor = AppColors.Text.secondary
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
            TextButton(
                onClick = {}
            ) {
                Text(
                    text = "Mot de passe oublié ?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Text.disabled,
                    modifier = Modifier
                )
            }
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
                    text = state.authState.message,
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