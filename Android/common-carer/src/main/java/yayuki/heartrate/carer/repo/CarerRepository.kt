package yayuki.heartrate.carer.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import yayuki.heartrate.carer.data.CarerExtra
import yayuki.heartrate.client.CarerClient
import yayuki.heartrate.client.repo.BaseUserRepository

class CarerRepository(
    private val client: CarerClient
) : BaseUserRepository<CarerExtra>(
    CarerExtra::class.java,
    client
) {
    override var _extra: CarerExtra = CarerExtra(HashMap())
    val stateFlow get() = client.stateFlow

    init {
        println(_extra.notFound.extra)
        GlobalScope.launch(Dispatchers.IO) {
            stateFlow.collect { event ->
                _extra.getPatient(event.id).takeIf {
                    it.user.id != -1
                }?.apply {
                    this.extra._hrValue = event.hrValue
                    this.extra._accuracy = event.accuracy
                    this.extra._latitude = event.latitude
                    this.extra._longitude = event.longitude
                }
            }
        }
    }
}