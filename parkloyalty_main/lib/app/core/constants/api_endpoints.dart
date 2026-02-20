class ApiEndpoints {
  static String baseUrl = 'https://devapi.parkloyalty.com/';
  static const String getDataSetWithoutToken =
      'informatics/get_dataset_no_token';
  static const String login = 'auth/site_officer_login';
  static const String welcome = 'screens/welcome_page';
  static const String template = 'templates/mobile/primary_template';
  static const String dataSet = 'informatics/get_dataset';
  static const String updateOfficer = 'l2-onboarder/update_site_officer';
  static const String updateActivity = 'event-logger/activity-update';
  static const String updateLocation = 'event-logger/location-update';
  static const String citationSimilarityCheck =
      'citations-issuer/citation_similarity_check';
  static const String issueCitationBook = 'citations/issue_citation_book';
  static const String getAnalyticsCount = 'analytics/mobile/get_counts';
  static const String getAnalyticsArrayCount =
      "analytics/mobile/get_array_counts";
  static const String getOfficerViolationCountData =
      "analytics/mobile/get_violation_counts_by_officer";
  static const String getActivityLog =
      "analytics/mobile/get_activity_updates_by_officer";
  static const String getLocations = "location-update/updates";
  static const String getCitations = "citations-issuer/ticket";
  static const String timingRecords = "parking-timing/mark";
  static const String getDailySummary = "analytics/officer_daily_summary";
  static const String mark = "parking-timing/mark/bulk";
  static const String getDataFromLrp = "informatics/get_data_from_lpr";
  static const String getPayByPlateAnalytics = "analytics/mobile/pay_by_plate";
  static const String bulkUpload = "static_file/bulk_upload";

  // static const String getPayBySpaceAnalytics = "analytics/mobile/pay_by_plate";
}
