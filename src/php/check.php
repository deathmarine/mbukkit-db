<?PHP
header('X-Chekkit-Version: 1.6');
require_once("./include/membersite_config.php");

$info = "get,fucked";
if(isset($_POST['submitted']))
{
   if($fgmembersite->Login())
   {
        echo "dbomaint,***********";
   }else{
		echo $info;
   }
}
?>
