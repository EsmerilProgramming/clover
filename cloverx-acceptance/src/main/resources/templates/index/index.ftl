<html>
<head>
  <title>Index with Template</title>
</head>
<body>
<h1 id="pageTitle">Index with template</h1>
<br/>

<form method="post" action="${request.exchange.resolvedPath}/index/index">

  <input type="text" id="name" name="name" />
  <button type="submit" id="submit">submit</button>
</form>


<form method="put" action="${request.exchange.resolvedPath}/index/put">

  <input type="text" id="putName" name="name" />
  <button type="submit" id="putSubmit">submit</button>
</form>
</body>
</html>