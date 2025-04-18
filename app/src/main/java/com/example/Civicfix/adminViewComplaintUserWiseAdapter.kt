package com.example.Civicfix

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminViewComplaintUserWiseAdapter(private val userList:ArrayList<UserData>) :
    RecyclerView.Adapter<AdminViewComplaintUserWiseAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.admin_complain_view_user_wise_list_item,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdminViewComplaintUserWiseAdapter.MyViewHolder, position: Int) {
        val currentitem=userList[position]

        holder.name.text=currentitem.name
        holder.phoneNo.text=currentitem.number
        holder.unSolvedComplaint.text= currentitem.ComplainsPending.toString()
        holder.solvedComplaint.text=currentitem.complainResolved.toString()
        holder.flatNo.text=currentitem.flatNo
        holder.buildingNo.text=currentitem.buildingNo
        holder.buildingName.text=currentitem.buildingName
        holder.userViewAllComplains.setOnClickListener{
            val intent= Intent(holder.itemView.context,user_complaint_detailed_view::class.java)
            intent.putExtra("userId",currentitem.userId)
            intent.putExtra("adminId",currentitem.adminId)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView =itemView.findViewById(R.id.admin_complaint_view_user_wise_user_name_text)
        val phoneNo: TextView =itemView.findViewById(R.id.admin_complaint_view_user_wise_phone_no_text)
        val unSolvedComplaint: TextView =itemView.findViewById(R.id.admin_complaint_view_user_wise_unsolved_complains_text)
        val solvedComplaint: TextView =itemView.findViewById(R.id.admin_complaint_view_user_wise_solved_complains_text)
        val userViewAllComplains: Button =itemView.findViewById(R.id.admin_complain_view_view_user_all_complaint_button)
        val flatNo:TextView=itemView.findViewById(R.id.admin_complaint_view_user_wise_flat_no_text)
        val buildingNo:TextView=itemView.findViewById(R.id.admin_complaint_view_user_wise_building_no_text)
        val buildingName:TextView=itemView.findViewById(R.id.admin_complaint_view_user_wise_building_name_text)
    }
}