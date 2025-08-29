import com.example.testsuite.*
import org.junit.jupiter.api.Test

@Suppress("UNUSED_VARIABLE", "USELESS_CAST")
class UsageTest {

    @Test
    fun `test 1`() {
        val k = MapsObject.newBuilder()
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

        val a1: Map<String, Int> = k.mapWithRegularValues
        val a2: Map<String, Int?> = k.mapWithNullableValues
        val a3: Map<String, Map<String, Int>> = k.mapWithRegularMapWithRegularValues
        val a4: Map<String, Map<String, Int?>> = k.mapWithRegularMapWithNullableValues
        val a5: Map<String, Map<String, Int>?> = k.mapWithNullableMapWithRegularValues
        val a6: Map<String, Map<String, Int?>?> = k.mapWithNullableMapWithNullableValues
        val a7: Map<String, Int>? = k.nullableMapWithRegularValues
        val a8: Map<String, Int?>? = k.nullableMapWithNullableValues
        val a9: Map<String, Map<String, Int>>? = k.nullableMapWithRegularMapWithRegularValues
        val a10: Map<String, Map<String, Int?>>? = k.nullableMapWithRegularMapWithNullableValues
        val a11: Map<String, Map<String, Int>?>? = k.nullableMapWithNullableMapWithRegularValues
        val a12: Map<String, Map<String, Int?>?>? = k.nullableMapWithNullableMapWithNullableValues

        k.mapWithRegularValues.remove("1")

        val l = ListsObject.newBuilder()
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

        val ll1: MutableList<Int> = l.listWithRegularValues
        val ll2: MutableList<Int>? = l.nullableListWithRegularValues
        val ll3: MutableList<Int?> = l.listWithNullableValues
        val ll4: MutableList<Int?>? = l.nullableListWithNullableValues
        val ll5: MutableList<MutableList<Int>> = l.listWithRegularListWithRegularValues
        val ll6: MutableList<MutableList<Int?>> = l.listWithRegularListWithNullableValues
        val ll7: MutableList<MutableList<Int>?> = l.listWithNullableListWithRegularValues
        val ll8: MutableList<MutableList<Int?>?> = l.listWithNullableListWithNullableValues
        val ll9: MutableList<MutableList<Int>>? = l.nullableListWithRegularListWithRegularValues
        val ll10: MutableList<MutableList<Int?>>? = l.nullableListWithRegularListWithNullableValues
        val ll11: MutableList<MutableList<Int>?>? = l.nullableListWithNullableListWithRegularValues
        val ll12: MutableList<MutableList<Int?>?>? = l.nullableListWithNullableListWithNullableValues

        l.listWithRegularValues = listOf(1) as List<Int>
        l.nullableListWithRegularValues = null as List<Int>?
        l.listWithNullableValues = listOf(null) as List<Int?>
        l.nullableListWithNullableValues = listOf(null) as List<Int?>?

        val jj = DomainClassWrapper.newBuilder()
            .setDomainClass(DomainClass.newBuilder()
                .setField1("1")
                .setField2(2)
                .build())
            .setIndependentEnum(EnumClass.SECOND)
            .setNullableDomainClass(null)
            .setNullableIndependentEnum(null)

        val j = jj.build()

        val b1: DomainClass = j.domainClass
        val b2: EnumClass = j.independentEnum
        val b3: DomainClass? = j.nullableDomainClass
        val b4: EnumClass? = j.nullableIndependentEnum

        val c1: DomainClass? = jj.domainClass
        val c2: EnumClass? = jj.independentEnum
        val c3: DomainClass? = jj.nullableDomainClass
        val c4: EnumClass? = jj.nullableIndependentEnum

        val c1b = jj.domainClassBuilder
        val c3b = jj.nullableDomainClassBuilder

        j.domainClass = DomainClass.newBuilder().setField2(3).setField1("3").build()

        println(j)
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
