# -*- coding: utf-8 -*-
# Generated by Django 1.11.1 on 2017-07-13 06:24
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Election',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('date', models.DateField()),
            ],
        ),
        migrations.CreateModel(
            name='Vote',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('roster', models.IntegerField()),
                ('signature', models.ImageField(blank=True, null=True, upload_to='')),
                ('election', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='vote.Election')),
            ],
        ),
        migrations.CreateModel(
            name='Voter',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('nroTui', models.CharField(max_length=20)),
                ('fullName', models.CharField(max_length=200)),
                ('rut', models.CharField(max_length=12)),
                ('department', models.CharField(max_length=50)),
            ],
        ),
        migrations.AddField(
            model_name='vote',
            name='voter',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='vote.Voter'),
        ),
    ]
