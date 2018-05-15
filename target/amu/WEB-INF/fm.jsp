<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>File Manager</title>
    <meta http-equiv="Content-Type"
          content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="${url}/styles/style.css"/>
    <link rel="shortcut icon" href="${url}/img/folder.png" type="image/gif"/>
</head>

<%--https://getbootstrap.com/--%>
<body bgcolor="#FFFFFF">

<p>

    <span style="color: rgb(255, 0, 0);">${actionresult}</span>

    <c:if test="${!fatalerror}">

<form name="files" action="${self}${path}" method="post">

    <table border="1">
        <tr>
            <td class="title" style="border-right-width: 0;">

                <c:set var="parentlink" value="" scope="request"/>

                <c:forEach var="parent" items="${folder.parents}" varStatus="status">

                    <c:choose>
                        <c:when test="${parent.isActive}">
                            <a href="${self}${parent.link}">${parent.display}</a>
                        </c:when>
                        <c:otherwise>
                            ${parent.display}
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${!status.last}">
                        &gt;
                        <c:set var="parentlink" value="${self}${parent.link}" scope="request"/>
                    </c:if>

                </c:forEach>

                &nbsp;

                <c:if test="${parentlink != ''}">
                    <a href="${parentlink}"><img src="${url}/img/up-one-dir.png" title="to parent folder" width="16"
                                                 height="16" alt="UP" border="0"></a>
                </c:if>

                &nbsp;

                <a href="${self}${path}"><img src="${url}/img/reload.png" title="reload folder" width="16" height="16"
                                              alt="RELOAD" border="0"></a>

            </td>

        </tr>

    </table>


    <table class="files">
        <thead>

        <tr>
            <td class="header-center" style="width: 5%;">
                <script>
                    function doChkAll(oChkBox) {
                        var bChecked = oChkBox.checked;
                        var docFrmChk = document.forms['files'].index;
                        for (var i = 0; i < docFrmChk.length; i++) {
                            docFrmChk[i].checked = bChecked;
                        }
                    }
                </script>
                <small>
                    Check all
                    <input type="checkbox" name="chkAll" onclick="doChkAll(this);">
                </small>
            </td>


            <td class="header-left" style="">
                <small>Filename</small>&nbsp;
                <a href="${self}${path}?sort=nu">
                    <img src="${url}/img/shift-up.png" title="sort by name ascending" width="16" height="16"
                         alt="SORTUP" border="0"></a>
                &nbsp;

                <a href="${self}${path}?sort=nd">

                    <img src="${url}/img/shift-down.png" title="sort by name descending" width="16" height="16"
                         alt="SORTDN" border="0"></a>

            </td>
            <td class="header-center" style="">
                <small>Size</small>
                &nbsp;
                <a href="${self}${path}?sort=su">
                    <img src="${url}/img/shift-up.png" title="sort by size ascending" width="16" height="16"
                         alt="SORTUP" border="0"></a>

                &nbsp;

                <a href="${self}${path}?sort=sd">
                    <img src="${url}/img/shift-down.png" title="sort by size descending" width="16" height="16"
                         alt="SORTDN" border="0"></a>


                &nbsp;

            </td>
            <td class="header-center" style="">
                <small>Last Modification</small>&nbsp;
                <a href="${self}${path}?sort=du">
                    <img src="${url}/img/shift-up.png" title="sort by date ascending" width="16" height="16"
                         alt="SORTUP" border="0"></a>
                &nbsp;

                <a href="${self}${path}?sort=dd">

                    <img src="${url}/img/shift-down.png" title="sort by date descending" width="16" height="16"
                         alt="SORTDN" border="0"></a>
            </td>

        </tr>

        </thead>

        <tbody>

        <c:forEach var="file" items="${folder.files}">


            <tr>
                <td class="row-right">

                    <c:if test="${file.isZip}">
                        <a href="${self}${path}?command=Unzip&index=${file.id}"> <img src="${url}/img/unpack.gif"
                                                                                      title="unzip ${file.name}"
                                                                                      width="16" height="16" alt="UNZIP"
                                                                                      border="0"></a>
                    </c:if>
                    <small><input type="checkbox" name="index" value="${file.id}"></small>
                </td>

                <td class="row-left">
                    <small><c:choose>
                        <c:when test="${file.isDirectory}">
                            <a href="${self}${file.path}"><img src="${url}/img/folder.png" title="folder" width="16"
                                                               height="16" alt="DIR" border="0"></a>
                            <a href="${self}${file.path}">${file.name}</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${self}${file.path}"><img src="${url}/img/file.gif" title="file" width="16"
                                                               height="16" alt="FILE" border="0"></a>
                            <a href="${self}${file.path}">${file.name}</a>
                        </c:otherwise>
                    </c:choose></small>
                </td>

                <td class="row-right">${file.size} </td>

                <td class="row-center">${file.lastModified}</td>

            </tr>

        </c:forEach>

        </tbody>
    </table>


            <td class="row-left"><input type="submit" name="command" value="Rename to" title="Rename selected file">

            <input name="renameto" type="text"></td>

            <td class="row-left"><input type="submit" name="command" value="Zip" title="Zip download files">
            </td>

</form>
</c:if>
</body>
</html>