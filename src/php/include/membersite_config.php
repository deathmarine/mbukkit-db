<?PHP
require_once("./include/fg_membersite.php");
$fgmembersite = new FGMembersite();
$fgmembersite->SetWebsiteName('server.modcrafting.com:2063');
$fgmembersite->SetAdminEmail('josh@modcrafting.com');
$fgmembersite->InitDB('localhost','root','*********','dbo_master','registrations');
$fgmembersite->SetRandomKey('********');
?>