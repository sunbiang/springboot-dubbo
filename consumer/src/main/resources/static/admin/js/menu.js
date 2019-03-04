!function() {
debugger
$.ajax({
    cache:true,
    type:"POST",
    url:"localMenu",
    dataType:"json",
    success: function(data) {
        debugger
        var html = "";
        var menu = $("menu")
        for(var i = 0; i<data.length;i++){
            switch(data[i].name) {
                case "仪表盘":
                    html+="<li><a href='"+data[i].resUrl+"' class='waves-effect'><i class='fa fa-dashboard' aria-hidden='true'>" +
                        "</i><span>"+data[i].name+"</span></a>"
                    continue;
                case "发布文章":
                    html+="<li><a href='"+data[i].resUrl+"' class='waves-effect'><i class='fa fa-pencil-square-o' aria-hidden='true'>" +
                        "</i><span>"+data[i].name+"</span></a>"
                    continue;
                case "文章管理":
                    html+="<li><a href='"+data[i].resUrl+"' class='waves-effect'><i class='fa fa-list' aria-hidden='true'>" +
                        "</i><span>"+data[i].name+"</span></a>"
                    continue;
                case "页面管理":
                    html+="<li><a href='"+data[i].resUrl+"' class='waves-effect'><i class='fa fa-file-text' aria-hidden='true'>" +
                        "</i><span>"+data[i].name+"</span></a>"
                    continue;
                case "评论管理":
                    html+="<li><a href='"+data[i].resUrl+"' class='waves-effect'><i class='fa fa-comments' aria-hidden='true'>" +
                        "</i><span>"+data[i].name+"</span></a>"
                    continue;
                case "分类/标签":
                    html+="<li><a href='"+data[i].resUrl+"' class='waves-effect'><i class='fa fa-tags' aria-hidden='true'>" +
                        "</i><span>"+data[i].name+"</span></a>"
                    continue;
                case "文件管理":
                    html+="<li><a href='"+data[i].resUrl+"' class='waves-effect'><i class='fa fa-cloud-upload' aria-hidden='true'>" +
                        "</i><span>"+data[i].name+"</span></a>"
                    continue;
                case "友链管理":
                    html+="<li><a href='"+data[i].resUrl+"' class='waves-effect'><i class='fa fa-link' aria-hidden='true'>" +
                        "</i><span>"+data[i].name+"</span></a>"
                    continue;
                case "系统设置":
                    html+="<li><a href='"+data[i].resUrl+"' class='waves-effect'><i class='fa fa-gear' aria-hidden='true'>" +
                        "</i><span>"+data[i].name+"</span></a>"
                    continue;
            }
        }
        $("#menu").append(html);
    }
})
}();