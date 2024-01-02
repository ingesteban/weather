package dev.esteban.sportinggoods.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dev.esteban.sportinggoods.R
import dev.esteban.sportinggoods.databinding.ForecastItemBinding
import dev.esteban.sportinggoods.utils.getFormattedDate
import dev.esteban.sportinggoods.utils.getFormattedSpeed
import dev.esteban.sportinggoods.utils.getUrlImage
import dev.esteban.sportinggoods.utils.getWindDirection
import dev.esteban.weather.domain.model.ForecastWeather

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {
    private var itemList: List<ForecastWeather> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(private val binding: ForecastItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ForecastWeather) {
            binding.apply {
                highTemperature.text = binding.root.context.getString(
                    R.string.label_high_temp,
                    item.highTemp.toString()
                )
                lowTemperature.text = binding.root.context.getString(
                    R.string.label_high_temp,
                    item.lowTemp.toString()
                )
                date.text = item.date.getFormattedDate(binding.root.context)
                windSpeed.text = item.windSpeed.getFormattedSpeed()
                windDirection.text = item.windDirection.getWindDirection()
                windDescription.text = item.windDescription
                weatherIcon.load(item.icon.getUrlImage()) {
                    crossfade(true)
                }
            }
        }
    }

    fun setItems(items: List<ForecastWeather>) {
        itemList = emptyList()
        itemList = items
        notifyDataSetChanged()
    }
}
