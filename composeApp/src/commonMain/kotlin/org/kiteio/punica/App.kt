package org.kiteio.punica

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.app_name

@Composable
fun App() = MaterialTheme {
    Scaffold { innerPadding ->
        Text(
            "你好 ${stringResource(Res.string.app_name)}。",
            modifier = Modifier.padding(innerPadding),
        )
    }
}