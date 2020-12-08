package yayuki.heartrate.android.carer

import yayuki.heartrate.android.user.BaseApplication
import yayuki.heartrate.carer.repo.CarerRepository
import yayuki.heartrate.client.carer.NettyCarerClient

class CarerApplication : BaseApplication() {
    override val client = NettyCarerClient()
    override val userRepository = CarerRepository(client)
}