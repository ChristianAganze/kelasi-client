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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    onAuthSuccess: (String) -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val sizeClass = adaptiveInfo.windowSizeClass.windowWidthSizeClass
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
        when (sizeClass) {
            WindowWidthSizeClass.EXPANDED, WindowWidthSizeClass.MEDIUM -> ExpandedLoginLayout(
                state = state,
                onRememberMeChange = viewModel::updateRememberMe,
                onEmailChange = viewModel::clearEmailError,
                onPasswordChange = viewModel::clearPasswordError,
                onLogin = viewModel::login,
                onDemoLogin = viewModel::loginAsDemo,
                onDismissError = viewModel::dismissError,
                isMedium = sizeClass == WindowWidthSizeClass.MEDIUM
            )

            else -> CenteredLoginLayout(
                state = state,
                onRememberMeChange = viewModel::updateRememberMe,
                onEmailChange = viewModel::clearEmailError,
                onPasswordChange = viewModel::clearPasswordError,
                onLogin = viewModel::login,
                onDemoLogin = viewModel::loginAsDemo,
                onDismissError = viewModel::dismissError
            )
        }
    }
}

@Composable
private fun ExpandedLoginLayout(
    state: AuthViewModelState,
    onRememberMeChange: (Boolean) -> Unit,
    onEmailChange: () -> Unit,
    onPasswordChange: () -> Unit,
    onLogin: (String, String) -> Unit,
    onDemoLogin: (String) -> Unit,
    onDismissError: () -> Unit,
    isMedium: Boolean = false
) {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(if (isMedium) 0.45f else 1f)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            VisualBrandingContent(
                isBrandingPanel = true,
                modifier = Modifier.padding(if (isMedium) 32.dp else 64.dp),
                compact = isMedium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(if (isMedium) 0.55f else 1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(if (isMedium) 24.dp else 32.dp)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AuthFormSection(
                    state = state,
                    onRememberMeChange = onRememberMeChange,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onLogin = onLogin,
                    onDemoLogin = onDemoLogin,
                    onDismissError = onDismissError
                )
                Spacer(modifier = Modifier.height(32.dp))
                AuthFooter()
            }
        }
    }
}

@Composable
private fun CenteredLoginLayout(
    state: AuthViewModelState,
    onRememberMeChange: (Boolean) -> Unit,
    onEmailChange: () -> Unit,
    onPasswordChange: () -> Unit,
    onLogin: (String, String) -> Unit,
    onDemoLogin: (String) -> Unit,
    onDismissError: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp)
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            VisualBrandingContent(
                isBrandingPanel = false,
                compact = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthFormSection(
                state = state,
                onRememberMeChange = onRememberMeChange,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onLogin = onLogin,
                onDemoLogin = onDemoLogin,
                onDismissError = onDismissError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthFooter()
        }
    }
}

@Composable
fun VisualBrandingContent(
    isBrandingPanel: Boolean,
    modifier: Modifier = Modifier,
    compact: Boolean = true
) {
    val textColor = if (isBrandingPanel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val titleStyle = when {
        isBrandingPanel && !compact -> MaterialTheme.typography.displayMedium
        isBrandingPanel && compact -> MaterialTheme.typography.headlineLarge
        compact -> MaterialTheme.typography.headlineLarge
        else -> MaterialTheme.typography.displaySmall
    }
    val taglineStyle = if (isBrandingPanel) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (isBrandingPanel) Alignment.Start else Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(if (compact) 28.dp else 32.dp)
                    .background(
                        if (isBrandingPanel) MaterialTheme
                            .colorScheme.onPrimary.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "K",
                    style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isBrandingPanel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Kelasi",
                style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = (-0.5).sp
            )
        }

        Spacer(modifier = Modifier.height(if (isBrandingPanel) (if (compact) 32.dp else 48.dp) else 24.dp))

        Text(
            text = "Bienvenue chez Kelasi",
            color = textColor,
            style = titleStyle,
            textAlign = if (isBrandingPanel) TextAlign.Start else TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "L'excellence au service de la digitalisation scolaire moderne et intuitive.",
            color = textColor.copy(alpha = 0.8f),
            style = taglineStyle,
            textAlign = if (isBrandingPanel) TextAlign.Start else TextAlign.Center,
            modifier = Modifier.fillMaxWidth(if (isBrandingPanel) 1f else 0.8f)
        )

        if (isBrandingPanel) {
            Spacer(modifier = Modifier.height(if (compact) 32.dp else 48.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(if (compact) 16.dp else 24.dp)
            ) {
                Text(
                    "Trouvez votre rythme d'apprentissage dès maintenant",
                    style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun AuthFooter(
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            "© 2026 DrcMind",
            color = textColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun AuthFormSection(
    state: AuthViewModelState,
    onRememberMeChange: (Boolean) -> Unit,
    onEmailChange: () -> Unit,
    onPasswordChange: () -> Unit,
    onLogin: (String, String) -> Unit,
    onDemoLogin: (String) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                RoundedCornerShape(24.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Connexion",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Veuillez entrer vos coordonnées pour accéder à votre compte.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                onEmailChange()
            },
            label = { Text("Email") },
            placeholder = { Text("nom@gmail.com") },
            leadingIcon = { Icon(Icons.Default.Mail, contentDescription = "Email", modifier = Modifier.size(20.dp)) },
            isError = state.emailError != null,
            supportingText = state.emailError?.let { { Text(it) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                onPasswordChange()
            },
            label = { Text("Mot de passe") },
            placeholder = { Text("••••••••") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Mot de passe", modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Masquer le mot de passe" else "Afficher le mot de passe",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { { Text(it) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onLogin(email, password) }),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

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
                    style = MaterialTheme.typography.bodySmall
                )
            }
            TextButton(
                onClick = { },
                enabled = false,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text(
                    text = "Mot de passe oublié ?",
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (state.errorMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = "Erreur",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = state.errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = onDismissError,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text(
                        "OK",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Button(
            onClick = { onLogin(email, password) },
            enabled = state.authState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth(),
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

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

        Text(
            text = "Accès Rapide / Démo Directe",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onDemoLogin("ROLE_SCHOOL_ADMIN") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Admin École", style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
            OutlinedButton(
                onClick = { onDemoLogin("ROLE_TEACHER") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Enseignant", style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onDemoLogin("ROLE_PARENT") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Parent", style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
            OutlinedButton(
                onClick = { onDemoLogin("ROLE_SUPER_USER") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Super Admin", style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
