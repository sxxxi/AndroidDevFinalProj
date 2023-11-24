package seiji.prog39402finalproject.data.mappers

interface RDMapper<R, D> {
    fun toDomain(r: R): D
    fun toRemote(d: D): R
}