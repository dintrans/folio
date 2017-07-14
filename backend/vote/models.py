from django.db import models

# Create your models here.
class Voter(models.Model):
    nroTui = models.CharField(max_length=20)
    fullName = models.CharField(max_length=100)
    lastName = models.CharField(max_length=100)
    rut = models.CharField(max_length=12)
    department = models.CharField(max_length=50)
    def __str__(self):
        return self.rut+", "+self.fullName+" "+self.lastName

class Election(models.Model):
    name = models.CharField(max_length=50)
    date = models.DateField()
    def __str__(self):
        return self.name

class Vote(models.Model):
    election = models.ForeignKey(Election)
    voter = models.ForeignKey(Voter)
    roster = models.IntegerField()
    signature = models.ImageField(null=True,blank=True)
    voteMethod = models.CharField(max_length=20)
    def __str__(self):
        return str(self.election)+", Folio "+str(self.roster)+", "+str(self.voter)