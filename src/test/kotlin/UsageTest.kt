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

        val mapVariable1: Map<String, Int> = mapsObject.mapWithRegularValues
        val mapVariable2: Map<String, Int?> = mapsObject.mapWithNullableValues
        val mapVariable3: Map<String, Map<String, Int>> = mapsObject.mapWithRegularMapWithRegularValues
        val mapVariable4: Map<String, Map<String, Int?>> = mapsObject.mapWithRegularMapWithNullableValues
        val mapVariable5: Map<String, Map<String, Int>?> = mapsObject.mapWithNullableMapWithRegularValues
        val mapVariable6: Map<String, Map<String, Int?>?> = mapsObject.mapWithNullableMapWithNullableValues
        val mapVariable7: Map<String, Int>? = mapsObject.nullableMapWithRegularValues
        val mapVariable8: Map<String, Int?>? = mapsObject.nullableMapWithNullableValues
        val mapVariable9: Map<String, Map<String, Int>>? = mapsObject.nullableMapWithRegularMapWithRegularValues
        val mapVariable10: Map<String, Map<String, Int?>>? = mapsObject.nullableMapWithRegularMapWithNullableValues
        val mapVariable11: Map<String, Map<String, Int>?>? = mapsObject.nullableMapWithNullableMapWithRegularValues
        val mapVariable12: Map<String, Map<String, Int?>?>? = mapsObject.nullableMapWithNullableMapWithNullableValues

        mapsObject.mapWithRegularValues = mapOf("1" to 1) as Map<String, Int>
        mapsObject.nullableMapWithNullableValues = null as Map<String, Int>?
        mapsObject.mapWithNullableValues = mapOf("1" to null) as Map<String, Int?>
        mapsObject.nullableMapWithNullableValues = null as Map<String, Int?>?
        mapsObject.mapWithRegularMapWithRegularValues = mapOf("1" to mapOf("1" to 1)) as Map<String, Map<String, Int>>
        mapsObject.mapWithRegularMapWithNullableValues = mapOf("1" to mapOf("1" to null)) as Map<String, Map<String, Int?>>
        mapsObject.mapWithNullableMapWithRegularValues = mapOf("1" to null) as Map<String, Map<String, Int>?>
        mapsObject.mapWithNullableMapWithNullableValues = mapOf("1" to null) as Map<String, Map<String, Int?>?>
        mapsObject.nullableMapWithRegularMapWithRegularValues = null as Map<String, Map<String, Int>>?
        mapsObject.nullableMapWithRegularMapWithNullableValues = null as Map<String, Map<String, Int?>>?
        mapsObject.nullableMapWithNullableMapWithRegularValues = null as Map<String, Map<String, Int>?>?
        mapsObject.nullableMapWithNullableMapWithNullableValues = null as Map<String, Map<String, Int?>?>?
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

        val listVariable1: MutableList<Int> = listsObject.listWithRegularValues
        val listVariable2: MutableList<Int>? = listsObject.nullableListWithRegularValues
        val listVariable3: MutableList<Int?> = listsObject.listWithNullableValues
        val listVariable4: MutableList<Int?>? = listsObject.nullableListWithNullableValues
        val listVariable5: MutableList<MutableList<Int>> = listsObject.listWithRegularListWithRegularValues
        val listVariable6: MutableList<MutableList<Int?>> = listsObject.listWithRegularListWithNullableValues
        val listVariable7: MutableList<MutableList<Int>?> = listsObject.listWithNullableListWithRegularValues
        val listVariable8: MutableList<MutableList<Int?>?> = listsObject.listWithNullableListWithNullableValues
        val listVariable9: MutableList<MutableList<Int>>? = listsObject.nullableListWithRegularListWithRegularValues
        val listVariable10: MutableList<MutableList<Int?>>? = listsObject.nullableListWithRegularListWithNullableValues
        val listVariable11: MutableList<MutableList<Int>?>? = listsObject.nullableListWithNullableListWithRegularValues
        val listVariable12: MutableList<MutableList<Int?>?>? = listsObject.nullableListWithNullableListWithNullableValues

        listsObject.listWithRegularValues = listOf(1) as List<Int>
        listsObject.nullableListWithRegularValues = null as List<Int>?
        listsObject.listWithNullableValues = listOf(null) as List<Int?>
        listsObject.nullableListWithNullableValues = listOf(null) as List<Int?>?
        listsObject.listWithRegularListWithRegularValues = listOf(listOf(1)) as List<List<Int>>
        listsObject.listWithRegularListWithNullableValues = listOf(listOf(null)) as List<List<Int?>>
        listsObject.listWithNullableListWithRegularValues = listOf(null) as List<List<Int>?>
        listsObject.listWithNullableListWithNullableValues = listOf(null) as List<List<Int?>?>
        listsObject.nullableListWithRegularListWithRegularValues = null as List<List<Int>>?
        listsObject.nullableListWithRegularListWithNullableValues = null as List<List<Int?>>?
        listsObject.nullableListWithNullableListWithRegularValues = null as List<List<Int>?>?
        listsObject.nullableListWithNullableListWithNullableValues = null as List<List<Int?>?>?
    }

//    @Test
//    fun `test 10`() {
//        val l = ListsObject.newBuilder()
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
//        val listWithRegularValuesWrongType1: MutableList<Int?> = l.listWithRegularValues
//        val listWithRegularValuesWrongType2: MutableList<Int>? = l.listWithRegularValues
//        val listWithRegularValuesWrongType3: MutableList<Int?>? = l.listWithRegularValues
//
//        l.listWithRegularValues = listOf(1) as List<Int?>
//        l.listWithRegularValues = listOf(1) as List<Int>?
//        l.listWithRegularValues = listOf(1) as List<Int?>?
//
//        val listWithNullableValuesWrongType1: MutableList<Int> = l.listWithNullableValues
//        val listWithNullableValuesWrongType2: MutableList<Int>? = l.listWithNullableValues
//        val listWithNullableValuesWrongType3: MutableList<Int?>? = l.listWithNullableValues
//
//        l.listWithNullableValues = listOf(1) as List<Int>
//        l.listWithNullableValues = listOf(1) as List<Int>?
//        l.listWithNullableValues = listOf(1) as List<Int?>?
//    }
}
