package com.drcmind.kelasisuite.ui.auth


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Layers
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
import androidx.compose.ui.text.style.TextOverflow
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
    val isExpanded = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.authState) {
        if (state.authState is AuthState.Success) {
            onAuthSuccess((state.authState as AuthState.Success).role)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isExpanded) {
            // Desktop Layout: Full split screen
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Side: Solid primary color with branding
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    VisualBrandingContent(
                        isExpanded = true,
                        modifier = Modifier.padding(64.dp)
                    )
                }

                // Right Side: Form centered
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .width(450.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AuthFormSection(
                            state = state,
                            onRememberMeChange = viewModel::updateRememberMe,
                            onLogin = viewModel::login,
                            isExpanded = true
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        AuthFooter(isDark = false)
                    }
                }
            }
        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        VisualBrandingContent(isExpanded = false)
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        AuthFormSection(
                            state = state,
                            onRememberMeChange = viewModel::updateRememberMe,
                            onLogin = viewModel::login,
                            isExpanded = false,
                            modifier = Modifier.fillMaxWidth() // Already handled by widthIn above
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        AuthFooter(isDark = false)
                    }
            }
        }
    }
}

@Composable
fun VisualBrandingContent(
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    val textColor = if (isExpanded) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (isExpanded) Alignment.Start else Alignment.CenterHorizontally
    ) {
        // Logo Section
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (isExpanded) MaterialTheme
                            .colorScheme.onPrimary.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "K",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpanded) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Kelasi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = (-0.5).sp
            )
        }

        Spacer(modifier = Modifier.height(if (isExpanded) 48.dp else 24.dp))

        Text(
            text = "Bienvenue chez Kelasi",
            color = textColor,
            fontSize = if (isExpanded) 40.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = if (isExpanded) TextAlign.Start else TextAlign.Center,
            lineHeight = if (isExpanded) 48.sp else 32.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "L'excellence au service de la digitalisation scolaire moderne et intuitive.",
            color = textColor.copy(alpha = 0.8f),
            fontSize = if (isExpanded) 18.sp else 14.sp,
            textAlign = if (isExpanded) TextAlign.Start else TextAlign.Center,
            modifier = Modifier.fillMaxWidth(if (isExpanded) 1f else 0.8f)
        )

        if (isExpanded) {
            Spacer(modifier = Modifier.height(48.dp))
            // Glass Card (Only for desktop branding side)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp)
            ) {
                Text(
                    "Trouvez votre rythme d'apprentissage dès maintenant",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Composable
fun AuthFooter(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val textColor = if (isDark) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) 
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            "© 2026 DrcMind",
            color = textColor,
            fontSize = 12.sp
        )
        TextButton(onClick = {

        }) {
            Text(
                "A propos",
                color = textColor,
                fontSize = 12.sp
            )
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
    val emailState = rememberTextFieldState("")
    val passwordState = rememberTextFieldState("")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                RoundedCornerShape(24.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title & Subtitle
        Text(
            text = "Connexion",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Veuillez entrer vos coordonnées pour accéder à votre compte.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        // Email Input
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "EMAIL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                state = emailState,
                placeholder = { Text("nom@gmail.com", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Mail, contentDescription = "Email", modifier = Modifier.size(20.dp)) },
                isError = state.emailError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
            if (state.emailError != null) {
                Text(
                    text = state.emailError,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "MOT DE PASSE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedSecureTextField(
                state = passwordState,
                placeholder = { Text("••••••••", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", modifier = Modifier.size(20.dp)) },
                isError = state.passwordError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
            if (state.passwordError != null) {
                Text(
                    text = state.passwordError,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Remember Me & Forgot Password
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.rememberMe,
                    onCheckedChange = onRememberMeChange,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Se souvenir de moi",
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp
                )
            }
            TextButton(onClick = {
                //TODO: implementer la logique si l'utilisateur a deja oublier son mot de passe
            },
                contentPadding =PaddingValues(0.dp)) {
                Text(
                    text = "Mot de passe oublié ?",
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Login Button
        Button(
            onClick = {
                onLogin(emailState.text.toString(),
                passwordState.text.toString())
            },
            modifier = Modifier
                .fillMaxWidth(),
               // .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (state.authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Se connecter", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

    }
}
