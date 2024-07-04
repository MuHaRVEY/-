/*updateChildren 매서드는 파이어베이스 리얼타임 데이터베이스 내용
특정 경로의 데이터를 업데이트 할 때 사용함.
한번에 여러 필드를 업데이트할 수 있음
주로 Map 객체를 사용하여 여러필드를 한번에 갱신함.
*/
//예제 1


// Firebase Database 참조 가져오기
DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

// 업데이트할 데이터 경로 설정
String userId = "userMe";
DatabaseReference userRef = databaseReference.child("users").child(userId);

// 업데이트할 데이터 준비 (Map 형식으로)
Map<String, Object> updates = new HashMap<>();
updates.put("name", "New Name");
updates.put("age", 25);
updates.put("city", "New City");

// 데이터베이스 업데이트
//addOnCompleteListener를 통해 업데이트 작업이 완료되었을 경우의 동작을 정의
userRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) { //작업이 완료되었을 경우
        if (task.isSuccessful()) {
            // 업데이트 성공
            Log.d("Firebase", "Data updated successfully.");
        } else {
            // 업데이트 실패
            Log.e("Firebase", "Data update failed.", task.getException());
        }
    }
});
