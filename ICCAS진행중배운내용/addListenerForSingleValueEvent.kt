private fun loadUserCoins() {
        val userId = currentUser.uid
        val userRef = database.child("users").child(userId)

        userRef.child("coins").addListenerForSingleValueEvent(object : ValueEventListener {
          /* 
          addListenerForSingleValueEvent는 Firebase Realtime Database의 데이터를 읽기 위한 메서드 중 하나
          이 메서드는 데이터베이스에서 단 한 번 데이터를 읽고, 더 이상 변경 사항을 감지하지 않음.
          따라서 주로 특정 시점의 데이터가 필요할 때 사용 됨.
          */
            override fun onDataChange(snapshot: DataSnapshot) {
                val coins = snapshot.getValue(Long::class.java) ?: 0L
                coinText.text = coins.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CatRoomActivity, "데이터베이스 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }
