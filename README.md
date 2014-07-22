Training Plan Viewer
====================

The plans is as folows (although it may now work perfectly just yet).

Although there are some included plans, you can created and use your own training plans
by creating XMl files and putting them in the TrainingPlans directory on the sdcard card 
[`*/sdcard/TrainingPlanXMLs*`]

The XML file is laid out as below. 
For each [`*AM|NOON|PM*`] you can have either a duration (in minutes) or a distance (in Km).
The `*effort*` attribute is used to decide on which icon will be display (currently recognised values are 
[`*Rest|Gentle|Easy|Fartlek|Steady|Tempo|Hard Fartlek|Hills|Quick|Intervals|CrossTrain|Race*`]).

The `*Help*` section is currently unused.

```
<?xml version="1.0" encoding="UTF-8"?>
<TrainingPlan name="" weeksduration="" source="">
	<[Book|Website|Magazine]/>
	<Help>
		<instructions/>
		<glossary>
			<term key="">value</term>
			<term key="">value</term>
		</glossary>
		<faq>
			<question key="">answer</question>
		</faq>
	</Help>
	<weeks>
		<week nbr="">
			<days>
				<day nbr="">
					<AM [duration|distance]="" effort="" type="" />
					<NOON [duration|distance]="" effort="" type="" />
					<PM [duration|distance]="" effort="" type="" />
				</day>
				<day nbr="">
					<AM [duration|distance]="" effort="" type="" />
					<NOON [duration|distance]="" effort="" type="" />
					<PM [duration|distance]="" effort="" type="" />
				</day>
			</days>
		</week>
		<week nbr="">
			<days>
				<day nbr="">
					<AM [duration|distance]="" effort="" type="" />
					<NOON [duration|distance]="" effort="" type="" />
					<PM [duration|distance]="" effort="" type="" />
				</day>
				<day nbr="">
					<AM [duration|distance]="" effort="" type="" />
					<NOON [duration|distance]="" effort="" type="" />
					<PM [duration|distance]="" effort="" type="" />
				</day>
			</days>
		</week>
	</weeks>
</TrainingPlan>
```


