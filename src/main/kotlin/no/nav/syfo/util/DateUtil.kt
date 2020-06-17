package no.nav.syfo.util

import java.time.OffsetDateTime
import java.util.*
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

fun getXMLGregorianCalendarNow(minusDays: Int): XMLGregorianCalendar {
    val gregorianCalendar = GregorianCalendar()
    gregorianCalendar.add(Calendar.DAY_OF_MONTH, minusDays)
    val datatypeFactory = DatatypeFactory.newInstance()
    return datatypeFactory.newXMLGregorianCalendar(gregorianCalendar)
}

fun convertToOffsetDateTime(dateGregorianCalendar: XMLGregorianCalendar): OffsetDateTime {
    val gregorianCalendar = dateGregorianCalendar.toGregorianCalendar()
    val zonedDateTime = gregorianCalendar.toZonedDateTime()
    return zonedDateTime.toOffsetDateTime()
}
