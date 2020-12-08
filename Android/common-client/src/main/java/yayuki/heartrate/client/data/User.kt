package yayuki.heartrate.client.data

data class User(val id: Int, val name: String)

data class UserExtra(val user: User, val extra: Extra)