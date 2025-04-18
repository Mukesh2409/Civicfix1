package com.example.Civicfix

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class UserComplainViewAdapter(private val userList:ArrayList<UserComplain>) :
    RecyclerView.Adapter<UserComplainViewAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.user_view_compain_list,
        parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserComplainViewAdapter.MyViewHolder, position: Int) {
        val currentitem=userList[position]

        holder.complain_type.text=currentitem.ComplainType
        holder.complain_subject.text=currentitem.ComplainSubject
        if(currentitem.Complainsolved=="Yes"){
            holder.verified.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        }else{
            holder.verified.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }
        holder.verified.text=currentitem.Complainsolved
        holder.userViewDetailedComplaintButton.setOnClickListener{
            val intent= Intent(holder.itemView.context,user_complaint_detailed_view::class.java)
            intent.putExtra("complainId",currentitem.ComplainId)
            intent.putExtra("userId",currentitem.userId)
           holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val complain_type:TextView=itemView.findViewById(R.id.user_complain_view_type_text)
        val complain_subject: TextView=itemView.findViewById(R.id.user_complain_view_subject_text)
        val verified:TextView=itemView.findViewById(R.id.user_complain_view_verified_text)
        val userViewDetailedComplaintButton:Button=itemView.findViewById(R.id.user_compliant_view_detailed_complaint)
    }
}