package seiji.prog39402finalproject.data.mappers

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import seiji.prog39402finalproject.data.remote.models.CapsuleRemoteModel
import seiji.prog39402finalproject.domain.models.Capsule

class CapsuleMapper : RDMapper<CapsuleRemoteModel, Capsule> {
    override fun toDomain(r: CapsuleRemoteModel): Capsule {
        return Capsule(
            id = r.id,
            title =r.title,
            body = r.body,
            epochCreate = r.epochCreate,
            coord = LatLng(r.coord.latitude, r.coord.longitude),
            geoHash = r.geoHash,
            images = r.imageLinks
        )
    }

    override fun toRemote(d: Capsule): CapsuleRemoteModel {
        return CapsuleRemoteModel(
            id = d.id,
            title = d.title,
            body = d.body,
            geoHash = d.geoHash,
            coord = GeoPoint(d.coord.latitude, d.coord.longitude),
            epochCreate = d.epochCreate,
            imageLinks = d.images
        )
    }
}