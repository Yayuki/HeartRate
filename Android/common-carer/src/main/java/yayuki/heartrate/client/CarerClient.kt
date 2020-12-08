package yayuki.heartrate.client

import kotlinx.coroutines.flow.SharedFlow
import yayuki.heartrate.client.UserClient
import yayuki.heartrate.carer.data.PatientStateEvent

interface CarerClient : UserClient {
    val stateFlow: SharedFlow<PatientStateEvent>
}