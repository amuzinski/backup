<html>
<head>
    <title>Login Page</title>
</head>

<body bgcolor="white" onLoad="setfocus()">
<form method="POST" action='<%= response.encodeURL("security_check") %>'>
    <table border="0" cellspacing="5">
        <tr>
            <th align="right">Username:</th>
            <td align="left"><input type="text" name="username"></td>
        </tr>
        <tr>
            <th align="right">Password:</th>
            <td align="left"><input type="password" name="password"></td>
        </tr>
        <tr>
            <td align="right"><input type="submit" id="submitbutton" value="Log In"></td>
        </tr>
    </table>
</form>

<script type="text/javascript">
    <!--
    document.forms[0].submitbutton.focus();
    //-->
</script>


</body>
</html>
