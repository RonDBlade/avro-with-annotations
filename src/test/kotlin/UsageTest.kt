import com.example.testsuite.DomainClass
import com.example.testsuite.DomainClassWrapper
import com.example.testsuite.EnumClass
import com.example.testsuite.MapsObject
import org.example.ManualPerson
import org.junit.jupiter.api.Test

class UsageTest {

    @Test
    fun `test`() {
        val k = MapsObject.newBuilder()
            .setMapWithRegularValues(mapOf("1" to 1))
            .setMapWithNullableValues(mapOf("2" to null))
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

        k.mapWithRegularValues

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

        val c1_b = jj.domainClassBuilder
        val c3_b = jj.nullableDomainClassBuilder

        j.domainClass = DomainClass.newBuilder().setField2(3).setField1("3").build()

        println(j)
    }
}