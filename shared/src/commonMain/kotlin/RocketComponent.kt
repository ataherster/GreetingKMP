import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.logging.Logger
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.math.log

class RocketComponent {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private suspend fun getDateOfLastSuccessfulLaunch(): String {
        println("httpClient.get()")
        val rockets: List<RocketLaunch> = httpClient.get("https://api.spacexdata.com/v4/launches").body()
        println("get lastSuccessLaunch")
        val lastSuccessLaunch = rockets.last { it.launchSuccess == true }
        println("parse date")
        val date = Instant.parse(lastSuccessLaunch.launchDateUTC).toLocalDateTime(TimeZone.currentSystemDefault())

        return "${date.month} ${date.dayOfMonth}, ${date.year}"
    }

    suspend fun launchPhrase (): String =
        try {
            "The last successful launch was on ${getDateOfLastSuccessfulLaunch()} \uD83D\uDE80"
        } catch (e: Exception) {

            println("Exception during get the date of the last successful launch $e")
            "Error occurred"
        }

}