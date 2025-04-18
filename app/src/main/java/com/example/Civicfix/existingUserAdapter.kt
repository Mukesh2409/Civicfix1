
package com.example.Civicfix

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExistingUserAdapter(private val userList:ArrayList<UserData>) :
    RecyclerView.Adapter<ExistingUserAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.existing_user_list_item,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExistingUserAdapter.MyViewHolder, position: Int) {
        val currentitem=userList[position]

        holder.name.text=currentitem.name
        holder.phoneNo.text=currentitem.number
        holder.flatNo.text=currentitem.flatNo
        holder.buildingNo.text=currentitem.buildingNo
        holder.buildingName.text=currentitem.buildingName
        holder.userViewAllComplains.setOnClickListener{
            val intent= Intent(holder.itemView.context,user_profile_view::class.java)
            intent.putExtra("currentUserId",currentitem.userId)
            intent.putExtra("userId",currentitem.adminId)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView =itemView.findViewById(R.id.existing_user_list_item_name_text)
        val phoneNo: TextView =itemView.findViewById(R.id.existing_user_list_item_phone_no_text)
        val userViewAllComplains: ImageButton =itemView.findViewById(R.id.existing_user_list_item_view_user_button)
        val flatNo:TextView=itemView.findViewById(R.id.existing_user_list_item_flat_no_text)
        val buildingNo:TextView=itemView.findViewById(R.id.existing_user_list_item_building_no_text)
        val buildingName:TextView=itemView.findViewById(R.id.existing_user_list_item_building_name_text)
    }
}