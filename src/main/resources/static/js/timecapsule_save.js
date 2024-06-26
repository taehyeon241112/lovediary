// 타입캡슐 쓰기
$(".active_btn").click(function() {
    const title = $("#capsule_title").val();
    const contents = getEditorContent();
    const open_date = $("#open_date").val();
    const idx = $("#timecapsule_idx").data('idx');

    if(!title) {
        alert("제목을 입력해주세요.");
        return;
    }

    if(!contents){
        alert("내용을 입력해주세요");
        return;
    }

    if(!open_date) {
        alert("열릴 날짜를 입력해주세요.");
        return;
    }

    let form_data = new FormData;
    form_data.append("title", title);
    form_data.append("contents", contents);
    form_data.append("openDate", open_date);
    if(idx !== undefined && idx !== null){
        form_data.append("idx", idx);
    }

    save(form_data);
});

function save(form_data, retry = false) {
    $.ajax({
        url: '/timecapsule/save',
        method: 'post',
        data : form_data,
        contentType: false,
        processData: false,
        success: function (data) {
            const msg = data.msg ?? null;
            if(msg ?? null) {
                alert(msg);
            }

            if(data.code === "200") {
                let idx = $("#timecapsule_idx").data('idx');
                if(idx !== undefined && idx !== null){
                    location.replace("/timecapsule/detail/"+idx);
                } else {
                    location.replace("/timecapsule")
                }

            }
        }, error: function () {
            if(!retry) save(form_data, true);
        }
    });
}