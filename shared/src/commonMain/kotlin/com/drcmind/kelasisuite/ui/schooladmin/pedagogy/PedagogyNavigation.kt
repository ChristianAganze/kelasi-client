package com.drcmind.kelasisuite.ui.schooladmin.pedagogy

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.drcmind.kelasisuite.navigation.Route

@Composable
fun PedagogyNavigation(
    modifier: Modifier = Modifier,
    pedagogyBackStack : NavBackStack<NavKey>){
    NavDisplay(
        modifier = modifier,
        backStack = pedagogyBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.Pedagogy.Scheduling> {
                Column {
                    Text("C'est l'interface de création. Le Préfet place les cours (déjà affectés dans Scolarité > Affectations) dans une grille hebdomadaire pour chaque classe.")
                    Text("Intelligence du système : L'interface bloque les conflits (ex: empêcher de placer M. Kabongo en 7ème A et en 8ème B le lundi à 8h00).")
                }
            }
            entry<Route.SchoolAdmin.Pedagogy.ProgramRadar> {
                Column {
                    Text("Le tableau de bord visuel comparant le Programme National au Réalisé.")
                }
            }

            entry<Route.SchoolAdmin.Pedagogy.Assignment> {
                Column {
                    Text("Assigner les professeurs aux cours (étape préalable obligatoire avant de faire l'horaire).")
                }
            }

            entry<Route.SchoolAdmin.Pedagogy.Preparation> {
                Column {
                    Text("Boîte de réception des fiches de préparation pour validation par le Préfet.")
                }
            }

            entry<Route.SchoolAdmin.Pedagogy.ClassLog> {
                Column {
                    Text("Audit des journaux générés automatiquement (basés sur l'Horaire des Cours défini plus haut).")
                }
            }

            entry<Route.SchoolAdmin.Pedagogy.Inspections> {
                Column {
                    Text("Rapports des visites de classes.")
                }
            }
        }
    )
}