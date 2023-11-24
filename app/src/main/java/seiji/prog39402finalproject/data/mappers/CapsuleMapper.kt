package seiji.prog39402finalproject.data.mappers

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel
import seiji.prog39402finalproject.domain.Capsule

class CapsuleMapper : RDMapper<CapsuleRemoteModel, Capsule> {
    override fun toDomain(r: CapsuleRemoteModel): Capsule {
        return Capsule(
            r.id,
            r.title,
            r.body,
            r.epochCreate,
            LatLng(r.coord.latitude, r.coord.longitude)
        )
    }

    override fun toRemote(d: Capsule): CapsuleRemoteModel {
        return CapsuleRemoteModel(
            d.id,
            d.title,
            d.body,
            d.epochCreate,
            GeoPoint(d.coord.latitude, d.coord.latitude)
        )
    }
}