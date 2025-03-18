package uk.ac.tees.mad.moodlog.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.moodlog.R
import uk.ac.tees.mad.moodlog.viewmodel.AuthScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavHostController, viewmodel: AuthScreenViewModel = koinViewModel()
) {
    val email by viewmodel.email.collectAsStateWithLifecycle()
    val password by viewmodel.password.collectAsStateWithLifecycle()
    val confirmPassword by viewmodel.confirmPassword.collectAsStateWithLifecycle()
    val isPasswordVisible by viewmodel.isPasswordVisible.collectAsStateWithLifecycle()

    val state by viewmodel.tabState.collectAsStateWithLifecycle()
    val titlesAndIcons = viewmodel.titlesAndIcons

    val focusManager = LocalFocusManager.current
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }
    val focusRequesterConfirmPassword = remember { FocusRequester() }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(
                            color = Color.White, shape = MaterialTheme.shapes.extraLarge
                        )
                        .padding(16.dp)
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(id = R.drawable.moodlog_logo),
                        contentDescription = "MoodLog Logo"
                    )
                }
                Text(
                    text = "MoodLog",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Text(
                    text = "Track your emotional journey",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                ) {
                    PrimaryTabRow(
                        selectedTabIndex = state,
                        containerColor = Color.Transparent,
                        divider = {}) {
                        titlesAndIcons.forEachIndexed { index, (title, icon) ->
                            LeadingIconTab(
                                selected = state == index,
                                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                unselectedContentColor = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.5f
                                ),
                                onClick = { viewmodel.updateTabState(index) },
                                text = {
                                    Text(
                                        text = title,
                                    )
                                },
                                icon = {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                    )
                                })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Email TextField
                OutlinedTextField(
                    value = email,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterEmail),
                    onValueChange = {
                        viewmodel.updateEmail(it)
                    },
                    label = {
                        Text(
                            text = "Email"
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Email,
                            contentDescription = "Email",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusRequesterPassword.requestFocus()
                    }),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Password TextField
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        viewmodel.updatePassword(it)
                    },
                    label = {
                        Text(
                            text = "Password"
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = "Password",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterPassword),
                    visualTransformation =
                        if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = if(state == 0) ImeAction.Done else ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                        focusRequesterConfirmPassword.requestFocus() },
                        onDone = {
                            focusManager.clearFocus()
                        }),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            viewmodel.togglePasswordVisibility()
                        }) {
                            Icon(
                                imageVector =
                                    if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Toggle Password Visibility",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    })

                AnimatedVisibility(state == 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Confirm Password TextField
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            viewmodel.updateConfirmPassword(it)
                        },
                        isError = if (confirmPassword.isNotBlank()) confirmPassword != password else false,
                        supportingText ={
                            if (confirmPassword.isNotBlank() && confirmPassword != password) {
                                Text(
                                    text = "Passwords do not match",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        } ,
                        label = {
                            Text(
                                text = "Confirm Password"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = "Confirm Password",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequesterConfirmPassword),
                        visualTransformation =
                            if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                        }),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                viewmodel.togglePasswordVisibility()
                            }) {
                                Icon(
                                    imageVector =
                                        if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Toggle Password Visibility",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        })
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    enabled = if (state == 0) {
                        email.isNotBlank() && password.isNotBlank()
                    } else {
                        email.isNotBlank() && password.isNotBlank()
                                && confirmPassword.isNotBlank() && confirmPassword == password
                    }, onClick = {
                        if (state == 0) {
                            //viewmodel.signIn(email, password)
                        } else {
                            //viewmodel.register(email, password)
                        }
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)
                ) {
                    if (state == 0) {
                        Icon(
                            Icons.AutoMirrored.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    } else {
                        Icon(
                            Icons.Filled.HowToReg,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (state == 0) "Sign In" else "Register",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state == 0) "Don't have an account?" else "Already have an account?",
                        textAlign = TextAlign.Center,
                    )
                    TextButton(onClick = {
                        viewmodel.switchTabState()
                    }) {
                        if (state == 0) {
                            Icon(
                                Icons.Default.HowToReg,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        } else {
                            Icon(
                                Icons.AutoMirrored.Filled.Login,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (state == 0) "Register" else "Sign In",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}