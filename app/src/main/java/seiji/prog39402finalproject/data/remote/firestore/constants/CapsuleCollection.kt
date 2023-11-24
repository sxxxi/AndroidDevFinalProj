package seiji.prog39402finalproject.data.remote.firestore.constants

object CapsuleCollection {
    const val name = "capsule"
    val fields = object {
        val title = "title"
        val body = "body"
        val dateCreated = "epoch_created"
        val coordinates = "coord"
    }
}