package yayuki.heartrate.android.carer.ui.state

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import yayuki.heartrate.android.carer.databinding.PatientListItemBinding
import yayuki.heartrate.carer.data.Patient

class PatientAdapter(
    private var list: List<Patient>,
    private val patientClick: PatientClick
) : RecyclerView.Adapter<PatientAdapter.ViewHolder>() {
    private val map: HashMap<Int, Int> = HashMap()

    init {
        list = list.sortedBy { it.user.id }
        list.forEachIndexed { index, patient ->
            map[patient.user.id] = index
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PatientListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindAll(list[position])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) super.onBindViewHolder(holder, position, payloads)
        else holder.bindHR(list[position].extra.hrValue)
    }

    override fun getItemCount(): Int = list.size

    fun updateState(id: Int) {
        map[id]?.run {
            notifyItemChanged(this, 0)
        }
    }

    inner class ViewHolder(private val binding: PatientListItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bindAll(patient: Patient) {
            binding.name.text = patient.user.name
            binding.hrValue.text = patient.extra.hrValue.toString()
            binding.root.setOnClickListener(this)
        }

        fun bindHR(value: Int) {
            binding.hrValue.text = value.toString()
        }

        override fun onClick(v: View?) {
            patientClick.onClick(list[adapterPosition])
        }
    }

    fun interface PatientClick {
        fun onClick(patient: Patient)
    }
}