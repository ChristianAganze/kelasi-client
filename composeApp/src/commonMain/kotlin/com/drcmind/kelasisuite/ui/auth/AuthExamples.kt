package com.drcmind.kelasisuite.ui.auth

/**
 * EXEMPLE D'UTILISATION DU SYSTÈME D'AUTHENTIFICATION
 * 
 * Cette section montre comment utiliser le système d'authentification
 * dans votre application Compose Multiplatform.
 */

// ============================================================================
// 1. DANS NavigationRoot (Navigation)
// ============================================================================

/*
@Composable
fun NavigationRoot() {
    val rootBackStack = rememberNavBackStack(Route.Auth)
    
    NavDisplay(
        backStack = rootBackStack,
        entryProvider = entryProvider {
            entry<Route.Auth> {
                AuthScreen(
                    onAuthSuccess = {
                        // Après connexion réussie, naviguer au dashboard
                        rootBackStack.add(Route.SystemAdmin)
                    }
                )
            }
            entry<Route.SystemAdmin> {
                SystemAdminAppScreen()
            }
        }
    )
}
*/

// ============================================================================
// 2. UTILISER LE VIEWMODEL DIRECTEMENT
// ============================================================================

/*
@Composable
fun MyCustomAuthScreen(viewModel: AuthViewModel = koinInject()) {
    val state by viewModel.state.collectAsState()
    
    // Mettre à jour les champs
    viewModel.updateEmail("user@example.com")
    viewModel.updatePassword("securePassword123")
    
    // Lancer la connexion
    LaunchedEffect(Unit) {
        viewModel.login()
    }
    
    // Afficher le résultat
    when (state.authState) {
        is AuthState.Loading -> {
            // Afficher un spinner
            CircularProgressIndicator()
        }
        is AuthState.Success -> {
            // Connexion réussie!
            Text("Bienvenue ${state.email}")
        }
        is AuthState.Error -> {
            // Afficher l'erreur
            Text("Erreur: ${(state.authState as AuthState.Error).message}")
        }
        else -> {}
    }
}
*/

// ============================================================================
// 3. ACCÉDER AU TOKEN APRÈS CONNEXION
// ============================================================================

/*
@Composable
fun Dashboard(settingsStorage: SettingsStorage = koinInject()) {
    val token = remember { settingsStorage.getToken() }
    
    if (token != null) {
        // L'utilisateur est connecté
        Text("Token: ${token.take(20)}...")
    }
}
*/

// ============================================================================
// 4. DÉCONNEXION
// ============================================================================

/*
@Composable
fun LogoutButton(settingsStorage: SettingsStorage = koinInject()) {
    Button(onClick = {
        settingsStorage.clearUserInfo()
        // Naviguer vers l'écran de login
    }) {
        Text("Déconnexion")
    }
}
*/

// ============================================================================
// 5. VALIDATION PERSONNALISÉE
// ============================================================================

/*
// Si vous voulez ajouter une validation personnalisée avant login:

fun validateCustom(email: String, password: String): Boolean {
    // Exemple: Vérifier que l'email est en domaine d'école
    if (!email.endsWith("@example-school.edu")) {
        return false
    }
    return true
}

// Dans AuthViewModel, modifier la fonction login():
fun login() {
    if (validateInputs() && validateCustom(_state.value.email, _state.value.password)) {
        // Continuer avec le login
    }
}
*/

// ============================================================================
// 6. INTÉGRATION AVEC UN REPOSITORY PERSONNALISÉ
// ============================================================================

/*
// Si vous avez un repository pour autre chose:

@Composable
fun MonEcran(
    authViewModel: AuthViewModel = koinInject(),
    monRepository: MonRepository = koinInject()
) {
    val authState by authViewModel.state.collectAsState()
    
    // Utiliser les deux
    if (authState.authState is AuthState.Success) {
        // Données de mon repo
        val data = monRepository.getDonnees()
    }
}
*/

// ============================================================================
// CONFIGURATION REQUISE DANS gradle.properties
// ============================================================================

/*
# S'assurer que vous avez:

# 1. Koin version compatible
koin_version=1.2.0  # ou version compatible avec votre projet

# 2. Kotlin multiplatform plugin
kotlin.multiplatform.enableGranularSourceSetsMetadata=true
kotlin.native.enableDeferredGlobalInitialization=true
*/

// ============================================================================
// POINTS IMPORTANTS
// ============================================================================

/*
1. INJECTION:
   - Utiliser koinInject() dans les @Composable
   - Koin est automatiquement initialisé dans App()
   
2. ÉTAT:
   - collectAsState() pour passer de Flow à State
   - Ne pas observer directement le Flow en Compose
   
3. NAVIGATION:
   - onAuthSuccess() callback pour naviguer
   - N'appeler la navigation QUE après succès
   
4. TOKEN:
   - Sauvegardé automatiquement après login
   - Injecté automatiquement dans requests Ktor
   - Persistant entre redémarrages app
   
5. ERREURS:
   - Affichées à l'utilisateur dans AuthScreen
   - Loggées dans console (LogLevel.BODY)
   - Pas de crash app en cas d'erreur
   
6. SÉCURITÉ:
   - Ne jamais logger les passwords
   - Utiliser HTTPS en production
   - Vérifier le certificat SSL
   - Implémenter refresh token si nécessaire
*/
