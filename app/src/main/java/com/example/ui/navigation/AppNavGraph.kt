package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.model.sampleHistoricalSpeeches
import com.example.model.samplePoems
import com.example.ui.screens.*
import com.example.ui.screens.finance.FinanceScreen
import com.example.viewmodel.*
import com.example.viewmodel.FinanceViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    oratoriaViewModel: OratoriaViewModel = viewModel(),
    chatViewModel: AisoChatViewModel = viewModel(),
    liveViewModel: AisoLiveViewModel = viewModel(),
    quotesViewModel: QuotesViewModel = viewModel(),
    affirmationViewModel: AffirmationViewModel = viewModel(),
    speechDraftViewModel: SpeechDraftViewModel = viewModel(),
    speechAnalysisViewModel: SpeechAnalysisViewModel = viewModel(),
    generalCultureViewModel: GeneralCultureViewModel = viewModel(),
    dictionaryViewModel: DictionaryViewModel = viewModel(),
    spellCheckerViewModel: SpellCheckerViewModel = viewModel(),
    historicalSpeechViewModel: HistoricalSpeechViewModel = viewModel(),
    poemViewModel: PoemViewModel = viewModel(),
    financeViewModel: FinanceViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HUB
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToChat = { navController.navigate(Routes.CHAT) },
                onNavigateToLive = { navController.navigate(Routes.LIVE) },
                onNavigateToQuotes = { navController.navigate(Routes.QUOTES) },
                onNavigateToHub = { navController.navigate(Routes.HUB) }
            )
        }

        composable(Routes.CHAT) {
            AisoChatScreen(
                viewModel = chatViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToLive = { navController.navigate(Routes.LIVE) }
            )
        }

        composable(Routes.LIVE) {
            AisoLiveScreen(
                viewModel = liveViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.QUOTES) {
            QuotesScreen(
                viewModel = quotesViewModel,
                onBack = { navController.popBackStack() },
                onPracticeAffirmation = { id ->
                    affirmationViewModel.selectAffirmation(id)
                    navController.navigate("affirmation_practice/$id")
                }
            )
        }

        composable(Routes.HUB) {
            MainScreen(
                viewModel = oratoriaViewModel,
                onNavigateToExercise = { navController.navigate(Routes.COURSE) },
                onNavigateToQuiz = { navController.navigate(Routes.QUIZ) },
                onNavigateToTechniques = { navController.navigate(Routes.TECHNIQUES) },
                onNavigateToToxic = { navController.navigate(Routes.TOXIC) },
                onNavigateToAnecdotes = { navController.navigate(Routes.ANECDOTES) },
                onNavigateToLiveDebate = { navController.navigate(Routes.LIVE_DEBATE) },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                onNavigateToImpromptu = { navController.navigate(Routes.IMPROMPTU) },
                onNavigateToPracticeList = { navController.navigate(Routes.PRACTICE_LIST) },
                onNavigateToStageMovement = { navController.navigate(Routes.STAGE_MOVEMENT) },
                onNavigateToFillerWords = { navController.navigate(Routes.FILLER_WORDS) },
                onNavigateToRhetoricalFigures = { navController.navigate(Routes.RHETORICAL_FIGURES) },
                onNavigateToOpeningClosing = { navController.navigate(Routes.OPENING_CLOSING) },
                onNavigateToEsquivaMuletillas = { navController.navigate(Routes.ESQUIVA_MULETILLAS) },
                onNavigateToOratoryRunner = { navController.navigate(Routes.ORATORY_RUNNER) },
                onNavigateToLiveSpeechPractice = { navController.navigate(Routes.LIVE_SPEECH_PRACTICE) },
                onNavigateToCustomerServicePractice = { navController.navigate(Routes.CUSTOMER_SERVICE_PRACTICE) },
                onNavigateToSalesPractice = { navController.navigate(Routes.SALES_PRACTICE) },
                onNavigateToElGranImpostor = { navController.navigate(Routes.EL_GRAN_IMPOSTOR) },
                onNavigateToQuotes = { navController.navigate(Routes.QUOTES) },
                onNavigateToCharades = { navController.navigate(Routes.CHARADES) },
                onNavigateToTongueTwisters = { navController.navigate(Routes.TONGUE_TWISTERS) },
                onNavigateToTongueTwisterDuel = { navController.navigate(Routes.TONGUE_TWISTER_DUEL) },
                onNavigateToBodyLanguage = { navController.navigate(Routes.BODY_LANGUAGE) },
                onNavigateToTongueTwisterOffline = { navController.navigate(Routes.TONGUE_TWISTER_OFFLINE) },
                onNavigateToAudienceSimulator = { navController.navigate(Routes.AUDIENCE_SIMULATOR) },
                onNavigateToRoute = { route ->
                    when (route) {
                        "aiso_chat" -> navController.navigate(Routes.CHAT)
                        "aiso_live" -> navController.navigate(Routes.LIVE)
                        "poems_list" -> navController.navigate(Routes.POEMS_LIST)
                        "historical_speeches_list" -> navController.navigate(Routes.HISTORICAL_SPEECHES_LIST)
                        "speech_analysis" -> navController.navigate(Routes.SPEECH_ANALYSIS)
                        "breathing_exercise" -> navController.navigate(Routes.BREATHING_EXERCISE)
                        "speech_drafts_list" -> navController.navigate(Routes.SPEECH_DRAFTS_LIST)
                        "ai_notes" -> navController.navigate(Routes.AI_NOTES)
                        "spell_checker" -> navController.navigate(Routes.SPELL_CHECKER)
                        "affirmations_list" -> navController.navigate(Routes.AFFIRMATIONS_LIST)
                        "dictionary" -> navController.navigate(Routes.DICTIONARY)
                        "calculator" -> navController.navigate(Routes.CALCULATOR)
                        "finances" -> navController.navigate(Routes.FINANCES)
                        "general_culture" -> navController.navigate(Routes.GENERAL_CULTURE)
                        "public_speaking_guide" -> navController.navigate(Routes.PUBLIC_SPEAKING_GUIDE)
                        "speech_timer" -> navController.navigate(Routes.SPEECH_TIMER)
                        "normal_timer" -> navController.navigate(Routes.NORMAL_TIMER)
                        "debate_timer" -> navController.navigate(Routes.DEBATE_TIMER)
                        "diction_practice" -> navController.navigate(Routes.DICTION_PRACTICE)
                        "oratory_defense" -> navController.navigate(Routes.ORATORY_DEFENSE)
                        "oratory_match" -> navController.navigate(Routes.ORATORY_MATCH)
                        "customer_service_quiz" -> navController.navigate(Routes.CUSTOMER_SERVICE_QUIZ)
                        "customer_service_practice" -> navController.navigate(Routes.CUSTOMER_SERVICE_PRACTICE)
                        "sales_practice" -> navController.navigate(Routes.SALES_PRACTICE)
                        "toxic_video" -> navController.navigate(Routes.TOXIC_VIDEO)
                        "live_speech_practice" -> navController.navigate(Routes.LIVE_SPEECH_PRACTICE)
                        "body_language" -> navController.navigate(Routes.BODY_LANGUAGE)
                        else -> {
                            try { navController.navigate(route) } catch (_: Exception) {}
                        }
                    }
                }
            )
        }

        // Additional Screens
        composable(Routes.COURSE) {
            CourseScreen(
                viewModel = oratoriaViewModel,
                onNavigateToQuiz = { navController.navigate(Routes.LESSON_CONTENT) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.LESSON_CONTENT) {
            val currentLesson by oratoriaViewModel.currentLesson.collectAsState()
            if (currentLesson != null) {
                LessonContentScreen(
                    lesson = currentLesson!!,
                    onBack = { navController.popBackStack() },
                    onStartQuiz = { navController.navigate(Routes.QUIZ) }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
        composable(Routes.QUIZ) {
            QuizScreen(
                viewModel = oratoriaViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.TECHNIQUES) {
            TechniquesListScreen(
                viewModel = oratoriaViewModel,
                onTechniqueClick = { technique ->
                    oratoriaViewModel.startTechniquePractice(technique)
                },
                onToxicParticipantsClick = { navController.navigate(Routes.TOXIC) },
                onAnecdotesClick = { navController.navigate(Routes.ANECDOTES) },
                onQuotesClick = { navController.navigate(Routes.QUOTES) },
                onNavigateToQuiz = { navController.navigate(Routes.LESSON_CONTENT) },
                onNavigateToRoute = { route ->
                    try { navController.navigate(route) } catch (_: Exception) {}
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.TOXIC) { ToxicParticipantsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.TOXIC_VIDEO) { ToxicVideoSimulationScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.ANECDOTES) { AnecdotesScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.LIVE_DEBATE) {
            LiveDebateScreen(
                viewModel = oratoriaViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.HISTORY) { HistoryScreen(viewModel = oratoriaViewModel, onBack = { navController.popBackStack() }) }
        composable(Routes.IMPROMPTU) {
            ImpromptuSpeechScreen(
                viewModel = oratoriaViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.PRACTICE_LIST) {
            PracticeListScreen(
                viewModel = oratoriaViewModel,
                onNavigateToExercise = { navController.navigate(Routes.COURSE) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.STAGE_MOVEMENT) { StageMovementGameScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.FILLER_WORDS) { FillerWordsGameScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.RHETORICAL_FIGURES) { RhetoricalFiguresGameScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.OPENING_CLOSING) { OpeningClosingGameScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.ESQUIVA_MULETILLAS) { EsquivaMuletillasGameScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.ORATORY_RUNNER) { OratoryRunnerScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.LIVE_SPEECH_PRACTICE) { LiveSpeechPracticeScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.CUSTOMER_SERVICE_PRACTICE) { CustomerServicePracticeScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SALES_PRACTICE) { SalesPracticeScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.EL_GRAN_IMPOSTOR) { ElGranImpostorScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.CHARADES) { CharadesGameScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.TONGUE_TWISTERS) { TongueTwisterGameScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.TONGUE_TWISTER_DUEL) { TongueTwisterDuelScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.BODY_LANGUAGE) { BodyLanguageScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.TONGUE_TWISTER_OFFLINE) { TongueTwisterOfflineScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.AUDIENCE_SIMULATOR) { AudienceSimulatorScreen(onBack = { navController.popBackStack() }) }

        composable(Routes.DICTION_PRACTICE) { DictionPracticeScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.ORATORY_DEFENSE) { OratoryDefenseScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.ORATORY_MATCH) { OratoryMatchScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.CUSTOMER_SERVICE_QUIZ) { CustomerServiceQuizScreen(onBack = { navController.popBackStack() }) }

        composable(Routes.SPEECH_TIMER) { SpeechTimerScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.NORMAL_TIMER) { NormalTimerScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.DEBATE_TIMER) { DebateTimerScreen(onBack = { navController.popBackStack() }) }

        composable(Routes.SPEECH_ANALYSIS) {
            SpeechAnalysisScreen(viewModel = speechAnalysisViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.HISTORICAL_SPEECHES_LIST) {
            HistoricalSpeechListScreen(
                speeches = sampleHistoricalSpeeches,
                onBack = { navController.popBackStack() },
                onSpeechClick = { id ->
                    historicalSpeechViewModel.selectSpeech(id)
                    navController.navigate("historical_speech_detail/$id")
                }
            )
        }
        composable(
            route = Routes.HISTORICAL_SPEECH_DETAIL,
            arguments = listOf(navArgument("speechId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("speechId")
            LaunchedEffect(id) {
                if (id != null) historicalSpeechViewModel.selectSpeech(id)
            }
            HistoricalSpeechDetailScreen(viewModel = historicalSpeechViewModel, onBack = { navController.popBackStack() })
        }

        composable(Routes.POEMS_LIST) {
            PoemListScreen(
                poems = samplePoems,
                onBack = { navController.popBackStack() },
                onPoemClick = { id ->
                    poemViewModel.selectPoem(id)
                    navController.navigate("poem_practice/$id")
                }
            )
        }
        composable(
            route = Routes.POEM_PRACTICE,
            arguments = listOf(navArgument("poemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("poemId")
            LaunchedEffect(id) {
                if (id != null) poemViewModel.selectPoem(id)
            }
            PoemPracticeScreen(viewModel = poemViewModel, onBack = { navController.popBackStack() })
        }

        composable(Routes.BREATHING_EXERCISE) { BreathingExerciseScreen(onBack = { navController.popBackStack() }) }

        composable(Routes.SPEECH_DRAFTS_LIST) {
            SpeechDraftListScreen(
                viewModel = speechDraftViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { draftId ->
                    navController.navigate("speech_draft_editor/${draftId ?: 0}")
                }
            )
        }
        composable(
            route = Routes.SPEECH_DRAFT_EDITOR,
            arguments = listOf(navArgument("draftId") { type = NavType.IntType })
        ) { backStackEntry ->
            val draftId = backStackEntry.arguments?.getInt("draftId") ?: 0
            SpeechDraftEditorScreen(
                viewModel = speechDraftViewModel,
                draftId = draftId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.AI_NOTES) { AINotesScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SPELL_CHECKER) {
            SpellCheckerScreen(viewModel = spellCheckerViewModel, onBack = { navController.popBackStack() })
        }

        composable(Routes.AFFIRMATIONS_LIST) {
            val affirmations by affirmationViewModel.affirmations.collectAsState()
            AffirmationListScreen(
                affirmations = affirmations,
                onBack = { navController.popBackStack() },
                onAffirmationClick = { id ->
                    affirmationViewModel.selectAffirmation(id)
                    navController.navigate("affirmation_practice/$id")
                }
            )
        }
        composable(
            route = Routes.AFFIRMATION_PRACTICE,
            arguments = listOf(navArgument("affirmationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("affirmationId")
            LaunchedEffect(id) {
                if (id != null) affirmationViewModel.selectAffirmation(id)
            }
            AffirmationPracticeScreen(
                viewModel = affirmationViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.DICTIONARY) {
            DictionaryScreen(viewModel = dictionaryViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.CALCULATOR) { CalculatorScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.FINANCES) { FinanceScreen(viewModel = financeViewModel, onBack = { navController.popBackStack() }) }
        composable(Routes.PUBLIC_SPEAKING_GUIDE) { PublicSpeakingGuideScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.GENERAL_CULTURE) {
            GeneralCultureScreen(viewModel = generalCultureViewModel, onBack = { navController.popBackStack() })
        }
    }
}
