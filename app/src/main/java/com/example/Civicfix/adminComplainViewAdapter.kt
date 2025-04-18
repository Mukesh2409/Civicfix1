package com.example.Civicfix

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class AdminComplainViewAdapter(private val AdminComplainList:ArrayList<AdminComplain>) :
    RecyclerView.Adapter<AdminComplainViewAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.admin_view_complain_list,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdminComplainViewAdapter.MyViewHolder, position: Int) {
        val currentAdmin=AdminComplainList[position]

        val userComplain = currentAdmin.userComplain
        holder.complain_type.text = userComplain.ComplainType
        holder.complain_subject.text = userComplain.ComplainSubject
        if(userComplain.Complainsolved=="Yes"){
            holder.verified.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        }else{
            holder.verified.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }
        holder.verified.text = userComplain.Complainsolved

        val userData=currentAdmin.userData
        holder.name.text=userData.name
        holder.number.text=userData.number
        holder.adminViewDetailedComplainButton.setOnClickListener {
            val intent= Intent(holder.itemView.context,admin_view_user_complaint_detailed_view::class.java)
            intent.putExtra("complainId",currentAdmin.userComplain.ComplainId)
            intent.putExtra("userId",currentAdmin.userComplain.userId)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return AdminComplainList.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val complain_type:TextView=itemView.findViewById(R.id.admin_view_complain_type_text)
        val complain_subject: TextView=itemView.findViewById(R.id.admin_view_complain_subject_text)
        val verified:TextView=itemView.findViewById(R.id.admin_view_complain_verified_text)
        val name:TextView=itemView.findViewById(R.id.admin_view_complain_name_text)
        val number:TextView=itemView.findViewById(R.id.admin_view_complain_number_text)
        val adminViewDetailedComplainButton: Button =itemView.findViewById(R.id.admin_view_complain_view_complain_detail_button)
    }
}