<template>
    <v-container>
        <v-row>
            <v-col>
                <v-sheet v-if="$vuetify.breakpoint.mdAndUp">
                    <v-toolbar flat>
                        <v-btn outlined class="mr-4" @click="setToday()">
                            {{ $t(appConstant.calendar.today) }}
                        </v-btn>
                        <v-btn fab text small @click="prev()">
                            <v-icon small>mdi-chevron-left</v-icon>
                        </v-btn>
                        <v-btn fab text small @click="next()">
                            <v-icon small>mdi-chevron-right</v-icon>
                        </v-btn>
                        <v-toolbar-title>{{ title }}</v-toolbar-title>
                        <v-spacer />
                        <v-menu bottom right>
                            <template v-slot:activator="{ on }">
                                <v-btn outlined class="mr-4" v-on="on">
                                    <span>{{ currentDimension }}{{ $t(appConstant.calendar.dimension) }}</span>
                                    <v-icon right>mdi-menu-down</v-icon>
                                </v-btn>
                            </template>
                            <v-list>
                                <v-list-item v-for="(count, index) in appConstant.dimensionLimit" :key="index" @click="updateDimension(count)">
                                    <v-list-item-title>{{ count }}{{ $t(appConstant.calendar.dimension) }}</v-list-item-title>
                                </v-list-item>
                            </v-list>
                        </v-menu>
                        <v-menu bottom right>
                            <template v-slot:activator="{ on }">
                                <v-btn
                                    outlined
                                    v-on="on"
                                >
                                    <span>{{ $t(typeToLabel[type]) }}</span>
                                    <v-icon right>mdi-menu-down</v-icon>
                                </v-btn>
                            </template>
                            <v-list>
                                <v-list-item @click="type = 'month'">
                                    <v-list-item-title>{{ $t(appConstant.calendar.month) }}</v-list-item-title>
                                </v-list-item>
                                <v-list-item @click="type = 'week'">
                                    <v-list-item-title>{{ $t(appConstant.calendar.week) }}</v-list-item-title>
                                </v-list-item>
                                <v-list-item @click="type = 'day'">
                                    <v-list-item-title>{{ $t(appConstant.calendar.day) }}</v-list-item-title>
                                </v-list-item>
                            </v-list>
                        </v-menu>
                    </v-toolbar>
                </v-sheet>
                <template v-else>
                    <v-sheet>
                        <v-toolbar dense flat>
                            <v-spacer />
                            <v-btn outlined class="mr-4" @click="setToday()">
                                {{ $t(appConstant.calendar.today) }}
                            </v-btn>
                            <v-spacer />
                            <v-menu bottom right>
                                <template v-slot:activator="{ on }">
                                    <v-btn outlined class="mr-4" v-on="on">
                                        <span>{{ currentDimension }}{{ $t(appConstant.calendar.dimension) }}</span>
                                        <v-icon right>mdi-menu-down</v-icon>
                                    </v-btn>
                                </template>
                                <v-list>
                                    <v-list-item v-for="(count, index) in appConstant.dimensionLimit" :key="index" @click="updateDimension(count)">
                                        <v-list-item-title>{{ count }}{{ $t(appConstant.calendar.dimension) }}</v-list-item-title>
                                    </v-list-item>
                                </v-list>
                            </v-menu>
                            <v-spacer />
                            <v-menu bottom right>
                                <template v-slot:activator="{ on }">
                                    <v-btn
                                        outlined
                                        v-on="on"
                                    >
                                        <span>{{ $t(typeToLabel[type]) }}</span>
                                        <v-icon right>mdi-menu-down</v-icon>
                                    </v-btn>
                                </template>
                                <v-list>
                                    <v-list-item @click="type = 'month'">
                                        <v-list-item-title>{{ $t(appConstant.calendar.month) }}</v-list-item-title>
                                    </v-list-item>
                                    <v-list-item @click="type = 'week'">
                                        <v-list-item-title>{{ $t(appConstant.calendar.week) }}</v-list-item-title>
                                    </v-list-item>
                                    <v-list-item @click="type = 'day'">
                                        <v-list-item-title>{{ $t(appConstant.calendar.day) }}</v-list-item-title>
                                    </v-list-item>
                                </v-list>
                            </v-menu>
                            <v-spacer />
                        </v-toolbar>
                    </v-sheet>
                    <v-sheet>
                        <v-toolbar dense flat>
                            <v-spacer />
                            <v-btn fab text small @click="prev()">
                                <v-icon small>mdi-chevron-left</v-icon>
                            </v-btn>
                            <v-spacer />
                            <v-toolbar-title>{{ title }}</v-toolbar-title>
                            <v-spacer />
                            <v-btn fab text small @click="next()">
                                <v-icon small>mdi-chevron-right</v-icon>
                            </v-btn>
                            <v-spacer />
                        </v-toolbar>
                    </v-sheet>
                </template>
                <v-sheet>
                    <v-calendar
                        ref="calendar"
                        v-model="focus"
                        :events="computedEvents"
                        :event-color="getEventColor"
                        :now="computedNow"
                        :type="type"
                        @click:event="showEvent"
                        @click:more="viewDay"
                        @click:date="viewDay"
                        @change="updateRange"
                    />
                    <v-menu
                        v-model="selectedOpen"
                        :close-on-content-click="false"
                        :activator="selectedElement"
                        :min-width="menuMinWidth"
                        light
                        offset-y
                    >
                        <v-card>
                            <time-slice v-if="selectedEvent" :time-slice-array="selectedEvent.timeArrays" />
                        </v-card>
                    </v-menu>
                </v-sheet>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import { appConstant } from '@/constant/constant.js';
import { parseTime } from '@/util/timeUtils.js';
import { singleTimeSliceApi } from '@/apiHttp/timeApi.js';
export default {
    name: 'TimeCalendar',
    components: {
        TimeSlice: () => import('@/component/time/TimeSlice.vue')
    },
    props: {
        timeSliceId: {
            type: String,
            default: '',
            required: true
        },
        dimension: {
            type: Number,
            default: 0,
            required: true
        }
    },
    data() {
        return {
            appConstant,
            singleTimeSlice: null,
            focus: '',
            type: 'month',
            typeToLabel: {
                month: appConstant.calendar.month,
                week: appConstant.calendar.week,
                day: appConstant.calendar.day
            },
            start: null,
            end: null,
            selectedEvent: {},
            selectedElement: null,
            selectedOpen: false,
            events: [],
            menuMinWidth: this.$vuetify.breakpoint.mdAndUp ? this.$vuetify.breakpoint.width * 0.6 : this.$vuetify.breakpoint.width * 0.95,
            currentDimension: appConstant.dimensionDefault
        };
    },
    computed: {
        title() {
            const { start, end } = this;
            if (!start || !end) {
                return '';
            }

            const monthFormat = this.$refs.calendar.getFormatter({
                timeZone: 'UTC',
                month: 'long'
            });

            const startMonth = monthFormat(start);
            const endMonth = monthFormat(end);
            const suffixMonth = startMonth === endMonth ? '' : endMonth;

            const startYear = start.year;
            const endYear = end.year;
            const suffixYear = startYear === endYear ? '' : endYear;

            const startDay = start.day;
            const endDay = end.day;

            switch (this.type) {
            case 'month':
                return `${startYear} ${startMonth}`;
            case 'week':
                return `${startYear} ${startMonth} ${startDay} - ${suffixYear} ${suffixMonth} ${endDay}`;
            case 'day':
                return `${startMonth} ${startDay} ${startYear}`;
            }
            return '';
        },
        computedEvents: function() {
            if (_.isNil(this.singleTimeSlice)) {
                return [];
            }
            const tsList = this.singleTimeSlice.dimensionTimeSliceList;
            if (_.isEmpty(tsList)) {
                return [];
            }
            // const events = [
            //     {
            //         name: 'aaaaaaaa',
            //         start: '2020-03-20 09:40:00',
            //         end: '2020-03-21 09:40:00',
            //         color: 'grey darken-1',
            //         timeArrays: null
            //     }
            // ];
            const events = [];
            tsList.forEach((it) => {
                events.push({
                    name: _.isNil(it.content) ? '' : it.content,
                    start: it.start,
                    end: it.end,
                    color: 'grey darken-1',
                    timeArrays: Array.of(it)
                });
            });
            return events;
        },
        computedNow: function() {
            if (_.isNil(this.singleTimeSlice)) {
                return parseTime(new Date().getTime());
            }
            return this.singleTimeSlice.now;
        }
    },
    created() {
        this.updateDimension(this.dimension);
    },
    methods: {
        viewDay({ date }) {
            this.focus = date;
            this.type = 'day';
        },
        getEventColor(event) {
            return event.color;
        },
        setToday() {
            this.focus = this.singleTimeSlice.now;
        },
        prev() {
            this.$refs.calendar.prev();
        },
        next() {
            this.$refs.calendar.next();
        },
        showEvent({ nativeEvent, event }) {
            console.log(event);
            const open = () => {
                this.selectedEvent = event;
                this.selectedElement = nativeEvent.target;
                setTimeout(() => {
                    this.selectedOpen = true;
                }, 10);
            };

            if (this.selectedOpen) {
                this.selectedOpen = false;
                setTimeout(open, 10);
            } else {
                open();
            }

            nativeEvent.stopPropagation();
        },
        updateRange({ start, end }) {
            console.log(start);
            this.start = start;
            this.end = end;
        },
        updateDimension(dimension) {
            if (_.isNaN(this.timeSliceId) || _.isNaN(this.dimension)) {
                return;
            }
            this.currentDimension = dimension;
            if (this.currentDimension > appConstant.dimensionLimit || this.currentDimension <= 0) {
                this.currentDimension = appConstant.dimensionDefault;
            }
            singleTimeSliceApi(this.timeSliceId, this.currentDimension).then(response => {
                const data = response.data;
                if (_.isNil(data)) {
                    return;
                }
                this.singleTimeSlice = data;
            });
            if (dimension === appConstant.dimensionDefault) {
                if (_.isNil(this.$route.query.dimension)) {
                    return;
                }
                this.$router.push({
                    query: {}
                });
            } else {
                if (_.toString(appConstant.dimensionDefault) !== this.$route.query.dimension) {
                    this.$router.push({
                        query: {
                            dimension: this.currentDimension
                        }
                    });
                }
            }
        }
    }
};
</script>
