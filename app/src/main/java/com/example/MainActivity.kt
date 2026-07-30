package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ExerciseScreen
import com.example.ui.screens.LessonContentScreen
import com.example.ui.screens.MainScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.TechniquesListScreen
import com.example.ui.screens.TechniquePracticeScreen
import com.example.viewmodel.OratoriaViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: OratoriaViewModel = viewModel()

                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(
                                viewModel = viewModel,
                                onNavigateToExercise = {
                                    navController.navigate("exercise")
                                },
                                onNavigateToQuiz = {
                                    navController.navigate("lesson_content")
                                },
                                onNavigateToTechniques = {
                                    navController.navigate("technique_chat")
                                },
                                onNavigateToToxic = {
                                    navController.navigate("toxic_participants")
                                },
                                onNavigateToAnecdotes = {
                                    navController.navigate("anecdotes")
                                },
                                onNavigateToLiveDebate = {
                                    navController.navigate("live_debate")
                                },
                                onNavigateToImpromptu = {
                                    navController.navigate("impromptu_speech")
                                },
                                onNavigateToPracticeList = {
                                    navController.navigate("practice_list")
                                },
                                onNavigateToStageMovement = {
                                    navController.navigate("stage_movement")
                                },
                                onNavigateToFillerWords = {
                                    navController.navigate("filler_words_game")
                                },
                                onNavigateToRhetoricalFigures = {
                                    navController.navigate("rhetorical_figures_game")
                                },
                                onNavigateToOpeningClosing = {
                                    navController.navigate("opening_closing_game")
                                },
                                onNavigateToEsquivaMuletillas = {
                                    navController.navigate("esquiva_muletillas_game")
                                },
                                onNavigateToOratoryRunner = {
                                    navController.navigate("oratory_runner")
                                },
                                onNavigateToLiveSpeechPractice = {
                                    navController.navigate("live_speech_practice")
                                },
                                onNavigateToCustomerServicePractice = {
                                    navController.navigate("customer_service_practice")
                                },
                                onNavigateToSalesPractice = {
                                    navController.navigate("sales_practice")
                                },
                                onNavigateToElGranImpostor = {
                                    navController.navigate("el_gran_impostor")
                                },
                                onNavigateToQuotes = {
                                    navController.navigate("quotes")
                                },
                                onNavigateToCharades = {
                                    navController.navigate("charades")
                                },
                                onNavigateToTongueTwisters = {
                                    navController.navigate("tongue_twisters")
                                },
                                onNavigateToTongueTwisterDuel = {
                                    navController.navigate("tongue_twister_duel")
                                },
                                onNavigateToBodyLanguage = {
                                    navController.navigate("body_language")
                                },
                                onNavigateToTongueTwisterOffline = {
                                    navController.navigate("tongue_twister_offline")
                                },
                                onNavigateToAudienceSimulator = {
                                    navController.navigate("audience_simulator")
                                },
                                onNavigateToRoute = { route ->
                                    navController.navigate(route)
                                }
                            )
                        }
                        composable("opening_closing_game") {
                            com.example.ui.screens.OpeningClosingGameScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("esquiva_muletillas_game") {
                            com.example.ui.screens.EsquivaMuletillasGameScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("el_gran_impostor") {
                            com.example.ui.screens.ElGranImpostorScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("charades") {
                            com.example.ui.screens.CharadesGameScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("tongue_twisters") {
                            com.example.ui.screens.TongueTwisterGameScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("tongue_twister_duel") {
                            com.example.ui.screens.TongueTwisterDuelScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("body_language") {
                            com.example.ui.screens.BodyLanguageScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("tongue_twister_offline") {
                            com.example.ui.screens.TongueTwisterOfflineScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("audience_simulator") {
                            com.example.ui.screens.AudienceSimulatorScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("diction_practice") {
                            com.example.ui.screens.DictionPracticeScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("oratory_runner") {
                            com.example.ui.screens.OratoryRunnerScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("oratory_match") {
                            com.example.ui.screens.OratoryMatchScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("live_speech_practice") {
                            com.example.ui.screens.LiveSpeechPracticeScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("customer_service_practice") {
                            com.example.ui.screens.CustomerServicePracticeScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("sales_practice") {
                            com.example.ui.screens.SalesPracticeScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("customer_service_quiz") {
                            com.example.ui.screens.CustomerServiceQuizScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("oratory_defense") {
                            com.example.ui.screens.OratoryDefenseScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("toxic_video") {
                            com.example.ui.screens.ToxicVideoSimulationScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("speech_timer") {
                            com.example.ui.screens.SpeechTimerScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("quotes") {
                            com.example.ui.screens.QuotesScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("rhetorical_figures_game") {
                            com.example.ui.screens.RhetoricalFiguresGameScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("practice_list") {
                            com.example.ui.screens.PracticeListScreen(
                                viewModel = viewModel,
                                onNavigateToExercise = { navController.navigate("exercise") },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("filler_words_game") {
                            com.example.ui.screens.FillerWordsGameScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("stage_movement") {
                            com.example.ui.screens.StageMovementGameScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("impromptu_speech") {
                            com.example.ui.screens.ImpromptuSpeechScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("live_debate") {
                            com.example.ui.screens.LiveDebateScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("anecdotes") {
                            com.example.ui.screens.AnecdotesScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("toxic_participants") {
                            com.example.ui.screens.ToxicParticipantsScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("technique_chat") {
                            val currentTechnique by viewModel.currentTechnique.collectAsState()
                            val aiFeedback by viewModel.aiFeedback.collectAsState()
                            val voiceMetrics by viewModel.voiceMetrics.collectAsState()
                            val isEvaluating by viewModel.isEvaluatingTechnique.collectAsState()
                            
                            if (currentTechnique != null) {
                                TechniquePracticeScreen(
                                    technique = currentTechnique!!,
                                    isEvaluating = isEvaluating,
                                    aiFeedback = aiFeedback,
                                    voiceMetrics = voiceMetrics,
                                    onEvaluate = { text ->
                                        viewModel.evaluateTechniquePractice(text)
                                    },
                                    onEvaluateWithAudio = { audioBytes ->
                                        viewModel.evaluateTechniquePracticeWithAudio(audioBytes)
                                    },
                                    onComplete = {
                                        viewModel.completeCurrentTechnique()
                                        viewModel.clearTechnique()
                                        navController.popBackStack()
                                    },
                                    onBack = {
                                        viewModel.clearTechnique()
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                        composable("lesson_content") {
                            val currentLesson by viewModel.currentLesson.collectAsState()
                            if (currentLesson != null) {
                                LessonContentScreen(
                                    lesson = currentLesson!!,
                                    onBack = {
                                        navController.popBackStack()
                                        viewModel.clearLesson()
                                    },
                                    onStartQuiz = {
                                        navController.navigate("quiz")
                                    }
                                )
                            }
                        }
                        composable("quiz") {
                            QuizScreen(
                                viewModel = viewModel,
                                onBack = {
                                    // Go back to course list
                                    navController.popBackStack("main", false)
                                    viewModel.clearLesson()
                                }
                            )
                        }
                        composable("exercise") {
                            ExerciseScreen(
                                viewModel = viewModel,
                                onBack = {
                                    navController.popBackStack()
                                    viewModel.clearSelection()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
