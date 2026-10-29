package com.example.motonav

import com.example.motonav.data.model.FuelEstimate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FuelCalculatorTest {

    @Test
    fun testFuelCalculation_correctFuelRequiredAndCosts() {
        val distanceKm = 150.0
        val mileageKmpl = 30.0
        val pricePerLitreInr = 100.0

        val estimate = FuelEstimate(
            distanceKm = distanceKm,
            mileageKmpl = mileageKmpl,
            pricePerLitreInr = pricePerLitreInr
        )

        // Fuel required = 150 / 30 = 5.0 Litres
        assertEquals(5.0, estimate.fuelRequiredLitres, 0.001)

        // One way cost = 5.0 * 100 = 500.0 INR
        assertEquals(500.0, estimate.oneWayCostInr, 0.001)

        // Round trip cost = 500.0 * 2 = 1000.0 INR
        assertEquals(1000.0, estimate.roundTripCostInr, 0.001)
    }

    @Test
    fun testFuelCalculation_zeroMileageHandledSafely() {
        val estimate = FuelEstimate(
            distanceKm = 100.0,
            mileageKmpl = 0.0,
            pricePerLitreInr = 102.50
        )

        assertEquals(0.0, estimate.fuelRequiredLitres, 0.001)
        assertEquals(0.0, estimate.oneWayCostInr, 0.001)
    }

    @Test
    fun testFuelCalculation_roundTripDoubleOneWay() {
        val estimate = FuelEstimate(
            distanceKm = 142.5,
            mileageKmpl = 28.5,
            pricePerLitreInr = 102.5
        )

        assertTrue(estimate.roundTripCostInr == estimate.oneWayCostInr * 2.0)
    }
}
