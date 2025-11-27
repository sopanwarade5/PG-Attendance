package com.example.pgattendance.ui
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pgattendance.R
import com.example.pgattendance.data.Student
class StudentAdapter(private var items: MutableList<Student>, private val onPresentClick: (Student) -> Unit, private val onEditClick: (Student) -> Unit, private val onDeleteClick: (Student) -> Unit) : RecyclerView.Adapter<StudentAdapter.VH>() {
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val mobile: TextView = view.findViewById(R.id.tvMobile)
        val room: TextView = view.findViewById(R.id.tvRoom)
        val btnPresent: Button = view.findViewById(R.id.btnPresent)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return VH(v)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = items[position]
        holder.name.text = s.name; holder.mobile.text = s.mobile; holder.room.text = s.room
        holder.btnPresent.setOnClickListener { onPresentClick(s) }; holder.btnEdit.setOnClickListener { onEditClick(s) }; holder.btnDelete.setOnClickListener { onDeleteClick(s) }
    }
    override fun getItemCount(): Int = items.size
    fun update(newList: List<Student>) { items.clear(); items.addAll(newList); notifyDataSetChanged() }
}
