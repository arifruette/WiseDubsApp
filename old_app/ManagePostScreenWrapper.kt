@Composable
fun ManagePostScreenWrapper(
    navigateBack: () -> Unit,
    requestInstantPostsRefresh: () -> Unit
) {
    val viewModel: ManagePostViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiEffect = viewModel.uiEffect
    val screenTitle = if (uiState.isEditMode) "Редактирование" else "Добавить объявление"
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            WiseDubsTopBar(
                title = screenTitle,
                leadingIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onLeadingIconClick = navigateBack
            )
        }
    ) { innerPadding ->
        ManagePostScreen(
            uiState = uiState,
            uiEffect = uiEffect,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navigateBack = navigateBack,
            requestInstantPostsRefresh = requestInstantPostsRefresh,
            onAction = viewModel::onAction
        )
    }
}