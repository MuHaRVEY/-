 private void checkAndResetDailyClears() {
        String userId = currentUser.getUid();
        DatabaseReference userRef = database.child("users").child(userId);

        userRef.child("lastResetDate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lastResetDate = snapshot.exists() ? snapshot.getValue(String.class) : "";
                String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

                if (!currentDate.equals(lastResetDate)) {
                    resetDailyClears(userRef, currentDate);
                } else {
                    loadUserCoins();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ServeGameBaseballActivity.this, "데이터베이스 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetDailyClears(DatabaseReference userRef, String currentDate) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("dailyClears", 0);
        updates.put("lastResetDate", currentDate);

        userRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadUserCoins();
                Toast.makeText(ServeGameBaseballActivity.this, "dailyClears가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ServeGameBaseballActivity.this, "초기화 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserCoins() {
        String userId = currentUser.getUid();
        DatabaseReference userRef = database.child("users").child(userId);

        userRef.child("coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long coins = snapshot.exists() ? snapshot.getValue(Long.class) : 0L;
                if (coins != null) {
                    coinText.setText(String.valueOf(coins));
                } else {
                    coinText.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ServeGameBaseballActivity.this, "데이터베이스 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndRewardCoins() {
        String userId = currentUser.getUid();
        DatabaseReference userRef = database.child("users").child(userId);

        userRef.child("dailyClears").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long dailyClears = snapshot.exists() ? (long) snapshot.getValue() : 0;

                if (dailyClears < maxClearsPerDay) {
                    rewardCoins(userRef, dailyClears);
                } else {
                    Toast.makeText(ServeGameBaseballActivity.this, "오늘은 더 이상 보상을 받을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    viewMode("end");
                    reset();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ServeGameBaseballActivity.this, "데이터베이스 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rewardCoins(DatabaseReference userRef, long dailyClears) {
        userRef.child("coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long coins = snapshot.exists() ? snapshot.getValue(Long.class) : 0L;
                if (coins != null) {
                    coins += coinReward;

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("coins", coins);
                    updates.put("dailyClears", dailyClears + 1);

                    Long finalCoins = coins;
                    userRef.updateChildren(updates).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ServeGameBaseballActivity.this, "정답입니다. " + coinReward + " 코인이 지급되었습니다.", Toast.LENGTH_SHORT).show();
                            coinText.setText(String.valueOf(finalCoins)); // 코인 텍스트 업데이트
                        } else {
                            Toast.makeText(ServeGameBaseballActivity.this, "코인 지급 오류", Toast.LENGTH_SHORT).show();
                        }
                        viewMode("end");
                        reset();
                    });
                } else {
                    Toast.makeText(ServeGameBaseballActivity.this, "코인 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ServeGameBaseballActivity.this, "데이터베이스 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
