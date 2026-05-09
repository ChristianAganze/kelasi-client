package com.drcmind.kelasisuite.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    onAuthSuccess: (String) -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {

    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val isExpanded =
        adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.authState) {
        if (state.authState is AuthState.Success) {
            onAuthSuccess((state.authState as AuthState.Success).role)
        }
    }
    Surface(Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuthFormSection(
                    state = state,
                    onRememberMeChange = viewModel::updateRememberMe,
                    onLogin = viewModel::login,
                    modifier = Modifier.weight(1f),
                    isExpanded = isExpanded
                )
                if (isExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(
                                color = MaterialTheme.colorScheme.primary, // visual-card-bg
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
fun VisualBrandingContent(
) {
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
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(50)
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Kelasi Suite",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Contenu Central
        Column {
            Text(
                text = "Bienvenue chez Kelasi",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.displayLargeEmphasized.fontSize,
                lineHeight = 44.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Kelasi aide les institution scolaire et académique à s'organiser et digitaliser la gestion de leur établissement.",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Simulation de la "Glass Card"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = 0.05f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp)
            ) {
                Text(
                    "Trouvez votre rythme d'apprentissage dès maintenant",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary,
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
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            TextButton(
                onClick = {}

            ) {
                Text(
                    "A propos",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun AuthFormSection(
    state: AuthViewModelState,
    onRememberMeChange: (Boolean) -> Unit,
    onLogin: (email: String, password: String) -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surfaceContainerHighest,
                RoundedCornerShape(
                    topStart = 32.dp,
                    bottomStart = 32.dp,
                    topEnd = if (!isExpanded) 32.dp else 0.dp,
                    bottomEnd = if (!isExpanded) 32.dp else 0.dp
                )
            )
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {

        val emailState = rememberTextFieldState("")
        val passwordState = rememberTextFieldState("")

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
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "K",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Kelasi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
        }

        // Title & Subtitle
        Text(
            text = "Connexion",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Veuillez entrer vos coordonnées pour accéder à votre compte.",
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Email Input

        OutlinedTextField(
            state = emailState,
            label = { Text("Adresse e-mail") },
            placeholder = { Text("nom@exemple.com") },
            leadingIcon = { Icon(Icons.Default.Mail, contentDescription = "Email") },
            isError = state.emailError != null,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )
        if (state.emailError != null) {
            Text(
                text = state.emailError,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedSecureTextField(
            state = passwordState,
            label = { Text("Password") },
            placeholder = { Text("••••••••") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            isError = state.passwordError != null,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium

        )

        if (state.passwordError != null) {
            Text(
                text = state.passwordError,
                color = MaterialTheme.colorScheme.error,
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
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Se souvenir de moi")
            }
            TextButton(
                onClick = {}
            ) {
                Text(
                    text = "Mot de passe oublié ?",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                )
            }
        }

        // Error State Display
        if (state.authState is AuthState.Error) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = state.authState.message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Login Button
        Button(
            onClick = { onLogin(emailState.text.toString(), passwordState.text.toString()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    strokeWidth = 2.dp
                )
            } else {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Se connecter")
            }
        }
    }
}