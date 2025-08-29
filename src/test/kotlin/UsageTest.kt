import com.example.testsuite.*
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
//        listsObject.nullableListWithRegularValues = listWithRegularValuesVariable
//        listsObject.nullableListWithRegularValues = listWithNullableValuesVariable
//        listsObject.nullableListWithRegularValues = nullableListWithNullableValuesVariable
//
//        val nullableListWithNullableValuesWrongType1: MutableList<Int> = nullableListWithNullableValuesVariable
//        val nullableListWithNullableValuesWrongType2: MutableList<Int?> = nullableListWithNullableValuesVariable
//        val nullableListWithNullableValuesWrongType3: MutableList<Int>? = nullableListWithNullableValuesVariable
//
//        listsObject.nullableListWithNullableValues = listWithRegularValuesVariable
//        listsObject.nullableListWithNullableValues = listWithNullableValuesVariable
//        listsObject.nullableListWithNullableValues = nullableListWithRegularValuesVariable
//    }
}
