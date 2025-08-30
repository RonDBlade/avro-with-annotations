import com.example.testsuite.*
import org.example.AAA
import org.junit.jupiter.api.Test

@Suppress("UNUSED_VARIABLE", "USELESS_CAST")
class UsageTest {

    @Test
    fun `map test`() {
        val mapsObject = MapsObject.newBuilder()
            .setMapWithRegularValues(mutableMapOf("1" to 1))
            .setMapWithNullableValues(mutableMapOf<String, Int?>("2" to null))
            .setMapWithRegularMapWithRegularValues(mapOf("1" to mapOf("10" to 10)))
            .setMapWithRegularMapWithNullableValues(mapOf("1" to mapOf("10" to null)))
            .setMapWithNullableMapWithRegularValues(mapOf("1" to null))
            .setMapWithNullableMapWithNullableValues(mapOf("1" to null))
            .setNullableMapWithRegularValues(mapOf("1" to 1))
            .setNullableMapWithNullableValues(mapOf("2" to null))
            .setNullableMapWithRegularMapWithRegularValues(mapOf("1" to mapOf("10" to 10)))
            .setNullableMapWithRegularMapWithNullableValues(mapOf("1" to mapOf("10" to null)))
            .setNullableMapWithNullableMapWithRegularValues(mapOf("1" to null))
            .setNullableMapWithNullableMapWithNullableValues(mapOf("1" to null))
            .build()

        val mapWithRegularValuesVariable: Map<String, Int> = mapsObject.mapWithRegularValues
        val mapWithNullableValuesVariable: Map<String, Int?> = mapsObject.mapWithNullableValues
        val mapWithRegularMapWithRegularValuesVariable: Map<String, Map<String, Int>> =
            mapsObject.mapWithRegularMapWithRegularValues
        val mapWithRegularMapWithNullableValuesVariable: Map<String, Map<String, Int?>> =
            mapsObject.mapWithRegularMapWithNullableValues
        val mapWithNullableMapWithRegularValuesVariable: Map<String, Map<String, Int>?> =
            mapsObject.mapWithNullableMapWithRegularValues
        val mapWithNullableMapWithNullableValuesVariable: Map<String, Map<String, Int?>?> =
            mapsObject.mapWithNullableMapWithNullableValues
        val nullableMapWithRegularValuesVariable: Map<String, Int>? = mapsObject.nullableMapWithRegularValues
        val nullableMapWithNullableValuesVariable: Map<String, Int?>? = mapsObject.nullableMapWithNullableValues
        val nullableMapWithRegularMapWithRegularValuesVariable: Map<String, Map<String, Int>>? =
            mapsObject.nullableMapWithRegularMapWithRegularValues
        val nullableMapWithRegularMapWithNullableValuesVariable: Map<String, Map<String, Int?>>? =
            mapsObject.nullableMapWithRegularMapWithNullableValues
        val nullableMapWithNullableMapWithRegularValuesVariable: Map<String, Map<String, Int>?>? =
            mapsObject.nullableMapWithNullableMapWithRegularValues
        val nullableMapWithNullableMapWithNullableValuesVariable: Map<String, Map<String, Int?>?>? =
            mapsObject.nullableMapWithNullableMapWithNullableValues

        mapsObject.mapWithRegularValues = mapWithRegularValuesVariable
        mapsObject.nullableMapWithNullableValues = mapWithNullableValuesVariable
        mapsObject.mapWithNullableValues = mapWithNullableValuesVariable
        mapsObject.nullableMapWithNullableValues = nullableMapWithNullableValuesVariable
        mapsObject.mapWithRegularMapWithRegularValues = mapWithRegularMapWithRegularValuesVariable
        mapsObject.mapWithRegularMapWithNullableValues = mapWithRegularMapWithNullableValuesVariable
        mapsObject.mapWithNullableMapWithRegularValues = mapWithNullableMapWithRegularValuesVariable
        mapsObject.mapWithNullableMapWithNullableValues = mapWithNullableMapWithNullableValuesVariable
        mapsObject.nullableMapWithRegularMapWithRegularValues = nullableMapWithRegularMapWithRegularValuesVariable
        mapsObject.nullableMapWithRegularMapWithNullableValues = nullableMapWithRegularMapWithNullableValuesVariable
        mapsObject.nullableMapWithNullableMapWithRegularValues = nullableMapWithNullableMapWithRegularValuesVariable
        mapsObject.nullableMapWithNullableMapWithNullableValues = nullableMapWithNullableMapWithNullableValuesVariable
    }

    @Test
    fun `list test`() {
        val listsObject = ListsObject.newBuilder()
            .setListWithRegularValues(listOf(1))
            .setNullableListWithRegularValues(listOf(2))
            .setListWithNullableValues(listOf(10))
            .setNullableListWithNullableValues(listOf(20))
            .setListWithRegularListWithRegularValues(listOf(listOf(3)))
            .setListWithRegularListWithNullableValues(listOf(listOf(30)))
            .setListWithNullableListWithRegularValues(listOf(listOf(4)))
            .setListWithNullableListWithNullableValues(listOf(listOf(40)))
            .setNullableListWithRegularListWithRegularValues(listOf(listOf(5)))
            .setNullableListWithRegularListWithNullableValues(listOf(listOf(50)))
            .setNullableListWithNullableListWithRegularValues(listOf(listOf(6)))
            .setNullableListWithNullableListWithNullableValues(listOf(listOf(60)))
            .build()

        val listWithRegularValuesVariable: MutableList<Int> = listsObject.listWithRegularValues
        val nullableListWithRegularValuesVariable: MutableList<Int>? = listsObject.nullableListWithRegularValues
        val listWithNullableValuesVariable: MutableList<Int?> = listsObject.listWithNullableValues
        val nullableListWithNullableValuesVariable: MutableList<Int?>? = listsObject.nullableListWithNullableValues
        val listWithRegularListWithRegularValuesVariable: MutableList<MutableList<Int>> =
            listsObject.listWithRegularListWithRegularValues
        val listWithRegularListWithNullableValuesVariable: MutableList<MutableList<Int?>> =
            listsObject.listWithRegularListWithNullableValues
        val listWithNullableListWithRegularValuesVariable: MutableList<MutableList<Int>?> =
            listsObject.listWithNullableListWithRegularValues
        val listWithNullableListWithNullableValuesVariable: MutableList<MutableList<Int?>?> =
            listsObject.listWithNullableListWithNullableValues
        val nullableListWithRegularListWithRegularValuesVariable: MutableList<MutableList<Int>>? =
            listsObject.nullableListWithRegularListWithRegularValues
        val nullableListWithRegularListWithNullableValuesVariable: MutableList<MutableList<Int?>>? =
            listsObject.nullableListWithRegularListWithNullableValues
        val nullableListWithNullableListWithRegularValuesVariable: MutableList<MutableList<Int>?>? =
            listsObject.nullableListWithNullableListWithRegularValues
        val nullableListWithNullableListWithNullableValuesVariable: MutableList<MutableList<Int?>?>? =
            listsObject.nullableListWithNullableListWithNullableValues

        listsObject.listWithRegularValues = listWithRegularValuesVariable
        listsObject.nullableListWithRegularValues = nullableListWithRegularValuesVariable
        listsObject.listWithNullableValues = listWithNullableValuesVariable
        listsObject.nullableListWithNullableValues = nullableListWithNullableValuesVariable
        listsObject.listWithRegularListWithRegularValues = listWithRegularListWithRegularValuesVariable
        listsObject.listWithRegularListWithNullableValues = listWithRegularListWithNullableValuesVariable
        listsObject.listWithNullableListWithRegularValues = listWithNullableListWithRegularValuesVariable
        listsObject.listWithNullableListWithNullableValues = listWithNullableListWithNullableValuesVariable
        listsObject.nullableListWithRegularListWithRegularValues = nullableListWithRegularListWithRegularValuesVariable
        listsObject.nullableListWithRegularListWithNullableValues = nullableListWithRegularListWithNullableValuesVariable
        listsObject.nullableListWithNullableListWithRegularValues = nullableListWithNullableListWithRegularValuesVariable
        listsObject.nullableListWithNullableListWithNullableValues = nullableListWithNullableListWithNullableValuesVariable


        val x1 = listsObject.listWithRegularValues
        val x2 = listsObject.nullableListWithRegularValues
        val x3 = listsObject.listWithNullableValues
        val x4: MutableList<Int?>? = listsObject.nullableListWithNullableValues
        val x5 = listsObject.listWithRegularListWithRegularValues
        val x6 = listsObject.listWithRegularListWithNullableValues
        val x7 = listsObject.listWithNullableListWithRegularValues
        val x8 = listsObject.listWithNullableListWithNullableValues
        val x9 = listsObject.nullableListWithRegularListWithRegularValues
        val x10 = listsObject.nullableListWithRegularListWithNullableValues
        val x11 = listsObject.nullableListWithNullableListWithRegularValues
        val x12 = listsObject.nullableListWithNullableListWithNullableValues

    }
//
//    @Test
//    fun `test bad conversions of list`() {
//        val listsObject = ListsObject.newBuilder()
//            .setListWithRegularValues(listOf(1))
//            .setNullableListWithRegularValues(listOf(2))
//            .setListWithNullableValues(listOf(10))
//            .setNullableListWithNullableValues(listOf(20))
//            .setListWithRegularListWithRegularValues(listOf(listOf(3)))
//            .setListWithRegularListWithNullableValues(listOf(listOf(30)))
//            .setListWithNullableListWithRegularValues(listOf(listOf(4)))
//            .setListWithNullableListWithNullableValues(listOf(listOf(40)))
//            .setNullableListWithRegularListWithRegularValues(listOf(listOf(5)))
//            .setNullableListWithRegularListWithNullableValues(listOf(listOf(50)))
//            .setNullableListWithNullableListWithRegularValues(listOf(listOf(6)))
//            .setNullableListWithNullableListWithNullableValues(listOf(listOf(60)))
//            .build()
//
//        val listWithRegularValuesVariable: MutableList<Int> = listsObject.listWithRegularValues
//        val nullableListWithRegularValuesVariable: MutableList<Int>? = listsObject.nullableListWithRegularValues
//        val listWithNullableValuesVariable: MutableList<Int?> = listsObject.listWithNullableValues
//        val nullableListWithNullableValuesVariable: MutableList<Int?>? = listsObject.nullableListWithNullableValues
//        val listWithRegularListWithRegularValuesVariable: MutableList<MutableList<Int>> =
//            listsObject.listWithRegularListWithRegularValues
//        val listWithRegularListWithNullableValuesVariable: MutableList<MutableList<Int?>> =
//            listsObject.listWithRegularListWithNullableValues
//        val listWithNullableListWithRegularValuesVariable: MutableList<MutableList<Int>?> =
//            listsObject.listWithNullableListWithRegularValues
//        val listWithNullableListWithNullableValuesVariable: MutableList<MutableList<Int?>?> =
//            listsObject.listWithNullableListWithNullableValues
//        val nullableListWithRegularListWithRegularValuesVariable: MutableList<MutableList<Int>>? =
//            listsObject.nullableListWithRegularListWithRegularValues
//        val nullableListWithRegularListWithNullableValuesVariable: MutableList<MutableList<Int?>>? =
//            listsObject.nullableListWithRegularListWithNullableValues
//        val nullableListWithNullableListWithRegularValuesVariable: MutableList<MutableList<Int>?>? =
//            listsObject.nullableListWithNullableListWithRegularValues
//        val nullableListWithNullableListWithNullableValuesVariable: MutableList<MutableList<Int?>?>? =
//            listsObject.nullableListWithNullableListWithNullableValues
//
//        // Test begins here
//
//        val listWithRegularValuesWrongType1: MutableList<Int?> = listWithRegularValuesVariable
//        val listWithRegularValuesWrongType2: MutableList<Int>? = listWithRegularValuesVariable
//        val listWithRegularValuesWrongType3: MutableList<Int?>? = listWithRegularValuesVariable
//
//        listsObject.listWithRegularValues = listWithNullableValuesVariable
//        listsObject.listWithRegularValues = nullableListWithRegularValuesVariable
//        listsObject.listWithRegularValues = nullableListWithNullableValuesVariable
//
//        val listWithNullableValuesWrongType1: MutableList<Int> = listWithNullableValuesVariable
//        val listWithNullableValuesWrongType2: MutableList<Int>? = listWithNullableValuesVariable
//        val listWithNullableValuesWrongType3: MutableList<Int?>? = listWithNullableValuesVariable
//
//        listsObject.listWithNullableValues = listWithRegularValuesVariable
//        listsObject.listWithNullableValues = nullableListWithRegularValuesVariable
//        listsObject.listWithNullableValues = nullableListWithNullableValuesVariable
//
//        val nullableListWithRegularValuesWrongType1: MutableList<Int> = nullableListWithRegularValuesVariable
//        val nullableListWithRegularValuesWrongType2: MutableList<Int?> = nullableListWithRegularValuesVariable
//        val nullableListWithRegularValuesWrongType3: MutableList<Int?>? = nullableListWithRegularValuesVariable
//
//        // listsObject.nullableListWithRegularValues = listWithRegularValuesVariable -> allowed
//        listsObject.nullableListWithRegularValues = listWithNullableValuesVariable
//        listsObject.nullableListWithRegularValues = nullableListWithNullableValuesVariable
//
//        val nullableListWithNullableValuesWrongType1: MutableList<Int> = nullableListWithNullableValuesVariable
//        val nullableListWithNullableValuesWrongType2: MutableList<Int?> = nullableListWithNullableValuesVariable
//        val nullableListWithNullableValuesWrongType3: MutableList<Int>? = nullableListWithNullableValuesVariable
//
//        listsObject.nullableListWithNullableValues = listWithRegularValuesVariable
//        // listsObject.nullableListWithNullableValues = listWithNullableValuesVariable -> allowed
//        listsObject.nullableListWithNullableValues = nullableListWithRegularValuesVariable
//    }

    @Test
    fun `v2`() {
        val aaa = AAA.builder()
            .v1("1")
            .v2(null)
            .v3(listOf("111"))
            .v4(listOf(null))
            .build()

        val v1Var: String = aaa.v1
        val v2Var: String? = aaa.v2
        val v3Var: List<String> = aaa.v3
        val v4Var: List<String?> = aaa.v4

//        // Test begins here
//
//        val v1Wrong: String? = v1Var
//
//        aaa.v1 = v2Var
//
//        val v2Wrong: String = v2Var
//
//        aaa.v2 = v1Var
//
//        val v3Wrong1: List<String?> = v3Var
//        val v3Wrong2: List<String>? = v3Var
//        val v3Wrong3: List<String?>? = v3Var
//
//        aaa.v3 = v3Wrong1
//        aaa.v3 = v3Wrong2
//        aaa.v3 = v3Wrong3
//
//        val v4Wrong1: List<String> = v4Var
//        val v4Wrong2: List<String>? = v4Var
//        val v4Wrong3: List<String?>? = v4Var

        val v4try1: List<String> = listOf("1")
        val v4try2: List<String>? = null
        val v4try3: List<String?>? = null


        aaa.v4 = v4try1
        aaa.v4 = v4try2
        aaa.v4 = v4try3

        val v3try1: List<String?> = listOf(null)
        val v3try2: List<String>? = null
        val v3try3: List<String?>? = null

        aaa.v3 = v3try1
        aaa.v3 = v3try2
        aaa.v3 = v3try3

        aaa.v1 = null
    }
}
