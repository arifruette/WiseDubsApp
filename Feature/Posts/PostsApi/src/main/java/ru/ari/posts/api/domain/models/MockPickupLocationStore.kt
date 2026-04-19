package ru.ari.posts.api.domain.models

object MockPickupLocationStore {

    private val locations = mutableListOf(
        PickupLocation(
            id = 1,
            userId = 1,
            corpus = "1",
            entrance = "A",
            floor = "2",
            room = "201",
            comment = "Возле лифта",
            displayText = "Корпус 1, Подъезд A, Этаж 2, Комната 201",
            createdAt = "2023-10-10T10:00:00Z",
            updatedAt = "2023-10-10T10:00:00Z"
        ),
        PickupLocation(
            id = 2,
            userId = 1,
            corpus = "2",
            entrance = null,
            floor = "1",
            room = "105",
            comment = null,
            displayText = "Корпус 2, Этаж 1, Комната 105",
            createdAt = "2023-10-11T12:00:00Z",
            updatedAt = "2023-10-11T12:00:00Z"
        )
    )

    private var nextId = 3

    fun getAll(): List<PickupLocation> = locations.toList()

    fun getById(id: Int): PickupLocation? = locations.find { it.id == id }

    fun create(location: PickupLocation): PickupLocation {
        val created = location.copy(id = nextId++)
        locations.add(created)
        return created
    }

    fun update(id: Int, transform: (PickupLocation) -> PickupLocation): PickupLocation? {
        val index = locations.indexOfFirst { it.id == id }
        if (index == -1) {
            return null
        }

        val updated = transform(locations[index]).copy(id = id)
        locations[index] = updated
        return updated
    }

    fun delete(id: Int): Boolean = locations.removeIf { it.id == id }
}
